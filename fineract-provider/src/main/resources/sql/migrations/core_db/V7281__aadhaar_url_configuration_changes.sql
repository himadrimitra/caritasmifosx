update c_external_service_properties cs
set cs.name = 'initUrl',
cs.value  = 'https://prod.aadhaarbridge.com/kua/'
where cs.name = 'otpUrl';


update c_external_service_properties cs
set cs.value  = 'https://prod.aadhaarbridge.com/kua/_kyc'
where cs.name = 'kycUrl';
