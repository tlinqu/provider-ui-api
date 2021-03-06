package gov.samhsa.c2s.provideruiapi.service;

import feign.FeignException;
import gov.samhsa.c2s.provideruiapi.config.ProviderUiApiProperties;
import gov.samhsa.c2s.provideruiapi.infrastructure.PcmClient;
import gov.samhsa.c2s.provideruiapi.infrastructure.dto.ConsentAttestationDto;
import gov.samhsa.c2s.provideruiapi.infrastructure.dto.ConsentDto;
import gov.samhsa.c2s.provideruiapi.infrastructure.dto.ConsentRevocationDto;
import gov.samhsa.c2s.provideruiapi.infrastructure.dto.IdentifiersDto;
import gov.samhsa.c2s.provideruiapi.service.dto.JwtTokenKey;
import gov.samhsa.c2s.provideruiapi.service.exception.DuplicateConsentException;
import gov.samhsa.c2s.provideruiapi.service.exception.PcmUserInterfaceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@Slf4j
public class PcmServiceImpl implements PcmService {
    private static final boolean CREATED_BY_PATIENT = false;
    private static final boolean UPDATED_BY_PATIENT = false;
    private static final boolean ATTESTED_BY_PATIENT = false;
    private static final boolean REVOKED_BY_PATIENT = false;
    private final PcmClient pcmClient;
    private final JwtTokenExtractor jwtTokenExtractor;
    private final ProviderUiApiProperties providerUiApiProperties;

    @Autowired
    public PcmServiceImpl(PcmClient pcmClient,
                          JwtTokenExtractor jwtTokenExtractor,
                          ProviderUiApiProperties providerUiApiProperties) {
        this.pcmClient = pcmClient;
        this.jwtTokenExtractor = jwtTokenExtractor;
        this.providerUiApiProperties = providerUiApiProperties;
    }

    @Override
    public Object getProviders(String mrn) {
        return pcmClient.getProviders(mrn);
    }

    @Override
    public void saveProviders(String mrn, IdentifiersDto providerIdentifiersDto) {
        pcmClient.saveProviders(mrn, providerIdentifiersDto);
    }

    @Override
    public void deleteProvider(String mrn, Long providerId) {
        pcmClient.deleteProvider(mrn, providerId);
    }

    @Override
    public Object getPurposes() {
        return pcmClient.getPurposes(LocaleContextHolder.getLocale());
    }

    @Override
    public Object getConsents(String mrn, Integer page, Integer size) {
        return pcmClient.getConsents(mrn, page, size, LocaleContextHolder.getLocale());
    }

    @Override
    public Object getConsent(String mrn, Long consentId, String format) {
        return pcmClient.getConsent(mrn, consentId, format);
    }

    @Override
    public void saveConsent(String mrn, ConsentDto consentDto) {
        // Get current user authId
        try {
            String createdBy = jwtTokenExtractor.getValueByKey(JwtTokenKey.USER_ID);
            pcmClient.saveConsent(mrn, consentDto, LocaleContextHolder.getLocale(), createdBy, CREATED_BY_PATIENT);
        } catch (FeignException fe) {
            int causedByStatus = fe.status();
            switch(causedByStatus) {
                case 409:
                    log.info("The specified patient already has this consent", fe);
                    throw new DuplicateConsentException("Already created same consent.");
                default:
                    log.error("Unexpected instance of FeignException has occurred", fe);
                    throw new PcmUserInterfaceException("An unknown error occurred while attempting to communicate with PCM service");
            }
        }
    }

    @Override
    public void updateConsent(String mrn, Long consentId, ConsentDto consentDto) {
        // Get current user authId
        String lastUpdatedBy = jwtTokenExtractor.getValueByKey(JwtTokenKey.USER_ID);
        pcmClient.updateConsent(mrn, consentId, consentDto, lastUpdatedBy, UPDATED_BY_PATIENT);

    }

    @Override
    public void deleteConsent(String mrn, Long consentId) {
        // Get current user authId
        String lastUpdatedBy = jwtTokenExtractor.getValueByKey(JwtTokenKey.USER_ID);
        pcmClient.deleteConsent(mrn, consentId, lastUpdatedBy);
    }

    @Override
    public Object getAttestedConsent(String mrn, Long consentId, String format) {
        return pcmClient.getAttestedConsent(mrn, consentId, format);
    }

    @Override
    public Object getRevokedConsent(String mrn, Long consentId, String format) {
        return pcmClient.getRevokedConsent(mrn, consentId, format);
    }

    @Override
    public void attestConsent(String mrn, Long consentId, ConsentAttestationDto consentAttestationDto) {
        // Get current user authId
        String attestedBy = jwtTokenExtractor.getValueByKey(JwtTokenKey.USER_ID);
        pcmClient.attestConsent(mrn, consentId, consentAttestationDto, attestedBy, ATTESTED_BY_PATIENT);
    }

    @Override
    public void revokeConsent(String mrn, Long consentId, ConsentRevocationDto consentRevocationDto) {
        // Get current user authId
        String revokedBy = jwtTokenExtractor.getValueByKey(JwtTokenKey.USER_ID);
        pcmClient.revokeConsent(mrn, consentId, consentRevocationDto, revokedBy, REVOKED_BY_PATIENT);
    }

    @Override
    public Object getConsentAttestationTerm() {
        return pcmClient.getConsentAttestationTerm(providerUiApiProperties.getConsentManagement().getActiveAttestationTermId(), LocaleContextHolder.getLocale());
    }

    @Override
    public Object getConsentRevocationTerm() {
        return pcmClient.getConsentRevocationTerm(providerUiApiProperties.getConsentManagement().getActiveRevocationTermId(), LocaleContextHolder.getLocale());
    }

    @Override
    public Object getConsentActivities(String mrn, Integer page, Integer size) {
        Locale selectedLocale = LocaleContextHolder.getLocale();
        return pcmClient.getConsentActivities(mrn, page, size, selectedLocale);

    }


}