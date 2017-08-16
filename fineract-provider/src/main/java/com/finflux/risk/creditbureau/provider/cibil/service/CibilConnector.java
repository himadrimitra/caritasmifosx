package com.finflux.risk.creditbureau.provider.cibil.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.commons.io.IOUtils;
import org.apache.fineract.infrastructure.configuration.data.CibilCredentialsData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.finflux.risk.creditbureau.provider.cibil.exception.CibilServerConnectionException;
import com.finflux.risk.creditbureau.provider.cibil.request.CibilRequest;
import com.finflux.risk.creditbureau.provider.cibil.response.CibilResponse;

public class CibilConnector {

    private static char truncateChar = (char) 19;
    private final static Logger logger = LoggerFactory.getLogger(CibilConnector.class);

    public final static CibilResponse getConsumerCreditReport(final CibilRequest cibilRequest, final CibilCredentialsData credentials) {
        CibilResponse response = null;
        String tuefPacket = cibilRequest.prepareTuefPacket();
        tuefPacket = tuefPacket + truncateChar;
        byte[] requestPacket = tuefPacket.getBytes();
        Socket socket = null;
        try {
            socket = new Socket(credentials.getHostName(), credentials.getPort());
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();
            out.write(requestPacket, 0, requestPacket.length);
            out.flush();
            byte[] receivedData = IOUtils.toByteArray(in);
            response = new CibilResponse(receivedData);
        } catch (IOException e) {
            throw new CibilServerConnectionException();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    logger.error("Socket closing is failed");
                }
            }

        }
        return response;
    }

    public final static byte[] getConsumerCreditReport(final String tuefRequest, final CibilCredentialsData credentials) {
        byte[] response = null;
        String tuefPacket = tuefRequest + truncateChar;
        byte[] requestPacket = tuefPacket.getBytes();
        Socket socket = null;
        try {
            socket = new Socket(credentials.getHostName(), credentials.getPort());
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();
            out.write(requestPacket, 0, requestPacket.length);
            out.flush();
            response = IOUtils.toByteArray(in);
        } catch (IOException e) {
            throw new CibilServerConnectionException();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    logger.error("Socket closing is failed");
                }
            }

        }
        return response;
    }
}
