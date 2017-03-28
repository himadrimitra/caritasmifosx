package com.finflux.mandates.processor;

import com.finflux.mandates.data.MandateProcessCounts;
import com.finflux.mandates.data.MandatesProcessData;
import com.finflux.mandates.domain.MandateProcessStatusEnum;
import com.finflux.mandates.exception.ProcessFailedException;
import com.finflux.mandates.fileformat.MandatesFileFormatHelper;
import com.finflux.mandates.service.MandatesProcessingReadPlatformService;
import com.finflux.mandates.service.MandatesProcessingStatusPlatformWriteService;
import com.finflux.portfolio.loan.mandate.data.MandateData;
import com.finflux.portfolio.loan.mandate.service.MandateReadPlatformService;
import org.apache.fineract.infrastructure.configuration.data.NACHCredentialsData;
import org.apache.fineract.infrastructure.configuration.service.ExternalServicesPropertiesReadPlatformService;
import org.apache.fineract.infrastructure.core.domain.FineractPlatformTenant;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.infrastructure.documentmanagement.contentrepository.ContentRepositoryUtils;
import org.apache.fineract.infrastructure.documentmanagement.data.FileData;
import org.apache.fineract.infrastructure.documentmanagement.domain.Document;
import org.apache.fineract.infrastructure.documentmanagement.service.DocumentReadPlatformService;
import org.apache.fineract.useradministration.domain.AppUser;
import org.apache.fineract.useradministration.domain.AppUserRepositoryWrapper;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class MandateDownloadProcessor {

        public static final String zipFileExtension = "application/zip";
        private final MandatesProcessingReadPlatformService mandatesProcessingReadPlatformService;
        private final MandatesProcessingStatusPlatformWriteService mandatesProcessingWritePlatformService;
        private final MandateReadPlatformService mandateReadPlatformService;
        private final DocumentReadPlatformService documentReadPlatformService;
        private final ExternalServicesPropertiesReadPlatformService externalServicesPropertiesReadPlatformService;
        private final ApplicationContext applicationContext;
        private final AppUserRepositoryWrapper userRepository ;
        private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

        @Autowired
        public MandateDownloadProcessor(final MandatesProcessingReadPlatformService mandatesProcessingReadPlatformService,
                final MandatesProcessingStatusPlatformWriteService mandatesProcessingWritePlatformService,
                final MandateReadPlatformService mandateReadPlatformService,
                final DocumentReadPlatformService documentReadPlatformService,
                final ExternalServicesPropertiesReadPlatformService externalServicesPropertiesReadPlatformService,
                final ApplicationContext applicationContext,
                final AppUserRepositoryWrapper userRepository){

                this.mandatesProcessingReadPlatformService = mandatesProcessingReadPlatformService;
                this.mandatesProcessingWritePlatformService = mandatesProcessingWritePlatformService;
                this.mandateReadPlatformService = mandateReadPlatformService;
                this.documentReadPlatformService = documentReadPlatformService;
                this.externalServicesPropertiesReadPlatformService = externalServicesPropertiesReadPlatformService;
                this.applicationContext = applicationContext;
                this.userRepository = userRepository;
        }

        public void processMandateDownload(final Long requestId){
                try{
                        MandatesProcessData processData = this.mandatesProcessingReadPlatformService.retrieveMandateProcessData(requestId);
                        Collection<MandateData> mandatesToProcess = this.mandateReadPlatformService
                                .retrieveRequestedMandates(processData.getOfficeId(), processData.includeChildOffices());
                        if(null != mandatesToProcess && mandatesToProcess.size() > 0){
                                this.mandatesProcessingWritePlatformService.updateProcessStatus(requestId, MandateProcessStatusEnum.INPROCESS, null,
                                        null, null, null);
                                FileSystem zipfs = null;
                                String fileLoc = generateTempFileLoc();
                                String zipFileName = "Mandates"+requestId+".zip";
                                ExecutorService executorService = null;
                                if(processData.includeMandateScans()){
                                        try {
                                                zipfs = createZipFileSystem(fileLoc, zipFileName);
                                                executorService = Executors.newFixedThreadPool(5);
                                                for(MandateData data : mandatesToProcess){
                                                        executorService.submit(new DocDownloadTask(data, zipfs, ThreadLocalContextUtil.getTenant()));
                                                }
                                        } catch (IOException e) {
                                                this.mandatesProcessingWritePlatformService.updateProcessStatus(requestId, MandateProcessStatusEnum.FAILED, null,
                                                        "error.unable.to.create.zip.file",""+e.getMessage(), null);
                                                return;
                                        }

                                }

                                FileData fileData = createDataFile(processData, mandatesToProcess);
                                Document document = null;
                                if(null != executorService){
                                        executorService.shutdown();
                                        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
                                }
                                if(null != zipfs){
                                        addFileToZipFileSystem(fileData.file(), fileData.name(), zipfs);
                                        zipfs.close();
                                        File zipFile = new File(fileLoc + File.separator + zipFileName);
                                        zipFile.length();
                                        FileInputStream in = new FileInputStream(zipFile);
                                        document = this.mandatesProcessingWritePlatformService.saveDocument(in, zipFileName, zipFile.length(), zipFileExtension);
                                        in.close();
                                        zipFile.delete();
                                        File tempZipDir = new File(fileLoc);
                                        tempZipDir.delete();
                                }else{
                                        document = this.mandatesProcessingWritePlatformService.saveDocument(fileData.file(), fileData.name(),
                                                new Long(fileData.file().available()), fileData.contentType());
                                }
                                this.mandatesProcessingWritePlatformService.updateMandateStatusAsInProcess(mandatesToProcess);
                                MandateProcessCounts counts = new MandateProcessCounts();
                                counts.setTotalRecords(mandatesToProcess.size());
                                this.mandatesProcessingWritePlatformService.updateProcessStatus(requestId, MandateProcessStatusEnum.PROCESSED, document.getId(), null, null, counts);

                        }else{
                                this.mandatesProcessingWritePlatformService.updateProcessStatus(requestId, MandateProcessStatusEnum.FAILED, null,
                                        "error.no.mandates.to.process", "No mandates found with Requested Status", null);
                        }
                }catch (Exception e){
                        this.mandatesProcessingWritePlatformService.updateProcessStatus(requestId, MandateProcessStatusEnum.FAILED, null,
                                "error.unknown", ""+e.getMessage(), null);
                        throw new ProcessFailedException(e);
                }
        }

        private FileData createDataFile(MandatesProcessData processData, Collection<MandateData> mandatesToProcess)
                throws IOException, InvalidFormatException {
                NACHCredentialsData nachProperties = this.externalServicesPropertiesReadPlatformService.getNACHCredentials();
                MandatesFileFormatHelper formatter = this.applicationContext.getBean(nachProperties.getPROCESSOR_QUALIFIER()+"MandatesFileFormatHelper",
                        MandatesFileFormatHelper.class);
                return formatter.formatDownloadFile(processData, nachProperties, mandatesToProcess);
        }

        private FileSystem createZipFileSystem(String zipFileLoc, String zipFileName) throws IOException {
                makeDirectories(zipFileLoc);
                Map<String, String> env = new HashMap<>();
                env.put("create", "true");
                Path path = Paths.get(zipFileLoc+File.separator+zipFileName);
                URI zipURI = URI.create(String.format("jar:file:%s", path.toUri().getPath()));
                return FileSystems.newFileSystem(zipURI, env);
        }

        private void makeDirectories(final String uploadDocumentLocation) {
                if (!new File(uploadDocumentLocation).isDirectory()) {
                        new File(uploadDocumentLocation).mkdirs();
                }
        }

        private void addFileToZipFileSystem(InputStream file, String fileName, FileSystem zipfs) throws IOException {
                OutputStream out = Files.newOutputStream(zipfs.getPath(fileName));
                IOUtils.copy(file, out);
                file.close();
                out.close();
        }

        private String generateTempFileLoc() {
                return System.getProperty("user.home") + File.separator + ".fineract"+ File.separator
                        + ThreadLocalContextUtil.getTenant().getName().replaceAll(" ", "").trim() + File.separator
                        + "temp"+ File.separator +ContentRepositoryUtils.generateRandomString() ;
        }

        private class DocDownloadTask implements Runnable {

                private final MandateData data;
                private final FileSystem zipfs;
                private final FineractPlatformTenant tenant;

                public DocDownloadTask(final MandateData data, final FileSystem zipfs, final FineractPlatformTenant tenant) {
                        this.data = data;
                        this.zipfs = zipfs;
                        this.tenant = tenant;
                }

                @Override
                public void run() {
                        ThreadLocalContextUtil.setTenant(this.tenant);
                        AppUser user = userRepository.fetchSystemUser();
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, user.getPassword(),
                                authoritiesMapper.mapAuthorities(user.getAuthorities()));
                        SecurityContextHolder.getContext().setAuthentication(auth);

                        FileData fileData = documentReadPlatformService.retrieveFileData("loans",
                                data.getLoanId(), data.getScannedDocumentId());
                        InputStream file = fileData.file();
                        try {
                                addFileToZipFileSystem(file, data.getLoanAccountNo()+fileData.name(), zipfs);
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                }
        }
}
