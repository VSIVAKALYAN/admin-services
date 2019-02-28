package io.mosip.kernel.applicanttype.api.impl;

import java.time.LocalDate;
import java.time.Period;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.applicanttype.api.constant.ApplicantTypeErrorCode;
import io.mosip.kernel.core.applicanttype.exception.InvalidApplicantArgumentException;
import io.mosip.kernel.core.applicanttype.spi.ApplicantType;
import io.mosip.kernel.core.util.DateUtils;

/**
 * Implementation for Applicant Type.
 * 
 * @author Bal Vikash Sharma
 *
 */
@Component
public class ApplicantTypeImpl implements ApplicantType {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicantTypeImpl.class);

	private static final String FOREIGNER = "FR";
	private static final String NON_FOREIGNER = "NFR";
	private static final String MALE = "MLE";
	private static final String FEMALE = "FLE";
	private static final String CHILD = "CHL";
	private static final String ADULT = "ADL";
	private static final String ATTR_INDIVIDUAL_TYPE = "individualTypeCode";
	private static final String ATTR_DATE_OF_BIRTH = "dateofbirth";
	private static final String ATTR_GENDER_TYPE = "genderCode";
	private static final String ATTR_BIOMETRIC_EXCEPTION_TYPE = "biometricAvailable";
	@Value("${mosip.kernel.applicant.type.age.limit}")
	private String ageLimit;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.applicanttype.spi.ApplicantCodeService#getApplicantType(
	 * java.util.Map)
	 */
	public String getApplicantType(Map<String, String> map) throws InvalidApplicantArgumentException {
		// queries to be check
		String itc = null;// Individual Type Code
		String dob = null;
		String genderType = null;
		boolean isBioExPresent = false;// Biometric Exception Type

		// insert the values for queries
		for (Entry<String, String> entry : map.entrySet()) {
			if (entry.getKey().equals(ATTR_INDIVIDUAL_TYPE)) {
				itc = entry.getValue();
			} else if (entry.getKey().equals(ATTR_DATE_OF_BIRTH)) {
				dob = entry.getValue();
			} else if (entry.getKey().equals(ATTR_GENDER_TYPE)) {
				genderType = entry.getValue();
			} else if (entry.getKey().equals(ATTR_BIOMETRIC_EXCEPTION_TYPE) && !isNullEmpty(entry.getValue())) {
				isBioExPresent = entry.getValue().equals("true");
			}
		}

		// check for NPE
		if (isNullEmpty(itc) || isNullEmpty(genderType) || isNullEmpty(dob)) {
			LOGGER.error("Illegal argument passed to applicant type");
			throw new InvalidApplicantArgumentException(ApplicantTypeErrorCode.INVALID_QUERY_EXCEPTION.getErrorCode(),
					ApplicantTypeErrorCode.INVALID_QUERY_EXCEPTION.getErrorMessage());

		}

		// calculate age
		Integer age = null;
		try {
			age = calculateAge(dob);
			if (age == null || age <= 0) {
				throw new InvalidApplicantArgumentException(
						ApplicantTypeErrorCode.INVALID_QUERY_EXCEPTION.getErrorCode(),
						ApplicantTypeErrorCode.INVALID_QUERY_EXCEPTION.getErrorMessage());
			}
		} catch (Exception e) {
			LOGGER.error("Error while claculating age");
			throw new InvalidApplicantArgumentException(
					ApplicantTypeErrorCode.INVALID_DATE_STRING_EXCEPTION.getErrorCode(),
					ApplicantTypeErrorCode.INVALID_DATE_STRING_EXCEPTION.getErrorMessage());
		}

		String ageCode = CHILD;

		try {
			if (age >= Integer.parseInt(ageLimit)) {
				ageCode = ADULT;
			}
		} catch (NumberFormatException e) {
			LOGGER.error("Error while setting age code");
			throw new InvalidApplicantArgumentException(ApplicantTypeErrorCode.INVALID_QUERY_EXCEPTION.getErrorCode(),
					ApplicantTypeErrorCode.INVALID_QUERY_EXCEPTION.getErrorMessage());
		}

		// check and return the applicant id
		return validateAndReturnApplicantType(itc, genderType, isBioExPresent, ageCode);
	}

	private boolean isNullEmpty(String str) {
		return str == null || str.trim().length() <= 0;
	}

	private String validateAndReturnApplicantType(String itc, String genderType, boolean isBioExPresent,
			String ageCode) {
		if (itc.equals(FOREIGNER) && genderType.equals(MALE) && ageCode.equals(CHILD) && !isBioExPresent) {
			// 1
			return "001";
		} else if (itc.equals(FOREIGNER) && genderType.equals(MALE) && ageCode.equals(ADULT) && !isBioExPresent) {
			// 2
			return "002";

		} else if (itc.equals(NON_FOREIGNER) && genderType.equals(MALE) && ageCode.equals(CHILD) && !isBioExPresent) {
			// 3
			return "003";

		} else if (itc.equals(NON_FOREIGNER) && genderType.equals(MALE) && ageCode.equals(ADULT) && !isBioExPresent) {
			// 4
			return "004";

		} else if (itc.equals(FOREIGNER) && genderType.equals(FEMALE) && ageCode.equals(CHILD) && !isBioExPresent) {
			// 5
			return "005";

		} else if (itc.equals(FOREIGNER) && genderType.equals(FEMALE) && ageCode.equals(ADULT) && !isBioExPresent) {
			// 6
			return "006";

		} else if (itc.equals(NON_FOREIGNER) && genderType.equals(FEMALE) && ageCode.equals(CHILD) && !isBioExPresent) {
			// 7
			return "007";

		} else if (itc.equals(NON_FOREIGNER) && genderType.equals(FEMALE) && ageCode.equals(ADULT) && !isBioExPresent) {
			// 8
			return "008";

		} else if (itc.equals(FOREIGNER) && genderType.equals(MALE) && ageCode.equals(CHILD) && isBioExPresent) {
			// 9
			return "009";

		} else if (itc.equals(FOREIGNER) && genderType.equals(MALE) && ageCode.equals(ADULT) && isBioExPresent) {
			// 10
			return "010";

		} else if (itc.equals(NON_FOREIGNER) && genderType.equals(MALE) && ageCode.equals(CHILD) && isBioExPresent) {
			// 11
			return "011";

		} else if (itc.equals(NON_FOREIGNER) && genderType.equals(MALE) && ageCode.equals(ADULT) && isBioExPresent) {
			// 12
			return "012";

		} else if (itc.equals(FOREIGNER) && genderType.equals(FEMALE) && ageCode.equals(CHILD) && isBioExPresent) {
			// 13
			return "013";

		} else if (itc.equals(FOREIGNER) && genderType.equals(FEMALE) && ageCode.equals(ADULT) && isBioExPresent) {
			// 14
			return "014";

		} else if (itc.equals(NON_FOREIGNER) && genderType.equals(FEMALE) && ageCode.equals(CHILD) && isBioExPresent) {
			// 15
			return "015";

		} else if (itc.equals(NON_FOREIGNER) && genderType.equals(FEMALE) && ageCode.equals(ADULT) && isBioExPresent) {
			// 16
			return "016";

		}
		return null;
	}

	private int calculateAge(String dob) {
		int age = 0;
		LocalDate birthDate = DateUtils.parseToLocalDateTime(dob).toLocalDate();
		LocalDate currentDate = LocalDate.now();
		if (birthDate != null && currentDate != null) {
			age = Period.between(birthDate, currentDate).getYears();
		}
		return age;
	}

}
