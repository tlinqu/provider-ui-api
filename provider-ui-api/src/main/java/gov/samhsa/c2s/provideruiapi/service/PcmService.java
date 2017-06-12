package gov.samhsa.c2s.provideruiapi.service;

import gov.samhsa.c2s.provideruiapi.infrastructure.dto.ConsentDto;
import gov.samhsa.c2s.provideruiapi.infrastructure.dto.IdentifiersDto;
import gov.samhsa.c2s.provideruiapi.infrastructure.dto.PageableDto;

import java.util.List;
import java.util.Locale;

public interface PcmService {
    List<Object> getProviders(String mrn);

    void saveProviders(String mrn, IdentifiersDto providerIdentifiersDto);

    void deleteProvider(String mrn, Long providerId);

    List<Object> getPurposes();

    PageableDto<Object> getConsents(String mrn, Integer page, Integer size);

    Object getConsent(String mrn, Long consentId, String format);

    void saveConsent(String mrn, ConsentDto consentDto, Locale locale);

    void updateConsent(String patientId, Long consentId, ConsentDto consentDto);

    void deleteConsent(String mrn, Long consentId);

    Object getAttestedConsent(String mrn, Long consentId, String format);

    Object getRevokedConsent(String mrn, Long consentId, String format);
}
