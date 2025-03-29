/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package train.booking.train.booking.dto.response;

public class ResponseCodes {

    public static final int REQUEST_SUCCESSFUL = 0;
    public static final int USER_ALREADY_LOGIN = 1;
    public static final int PARTIAL_SUCCESS = 10;
    public static final int AWAITING_APPROVAL = 15;

    public static final int INVALID_PHONE_NUMBER = 100;
    public static final int INVALID_EMAIL = 101;
    public static final int BAD_INPUT_PARAM = 102;
    public static final int CACHE_ERROR = 103;
    public static final int RESERVED_SUBDOMAIN = 104;
    public static final int FORGOT_PASSWORD_REQUEST_EXIST = 105;
    public static final int EXPIRED_TOKEN = 106;
    public static final int WRONG_TOKEN = 107;
    public static final int PARSER_EXCEPTION = 108;
    public static final int INPUT_ERROR = 109;
    
    public static final int MAX_SUBMISSION = 110;
    public static final int MAX_DISK_SIZE = 111;
    public static final int MAX_SUBUSERS = 112;;
    public static final int MAX_FORMS = 113;

    public static final int INVALID_ACCOUNT = 200;
    public static final int ALREADY_EXISTS = 201;

    public static final int ACCOUNT_DOES_NOT_EXIST = 202;
    public static final int ACCESS_DENIED = 203;
    public static final int PASSWORD_MISMATCH = 204;
    public static final int INVALID_CREDENTIALS = 205;
    public static final int WALLET_NOT_FOUND = 206;
    public static final int ACCOUNT_DISABLED = 207;
    public static final int EMAIL_ALREADY_EXISTS = 208;
    public static final int SUBDOMAIN_ALREADY_EXISTS = 209;
    public static final int ACCOUNT_AUTHENTICATION_ERROR = 210;
    public static final int ACCOUNT_UPDATE_FAILED = 211;
    public static final int USER_NOT_LOGGED_IN = 212;
    public static final int USER_UNAUTHORIZED = 213;
    public static final int FORM_DELETED = 214;

    public static final int OPERATION_NOT_PERMITTED = 220;
    public static final int OPERATION_NOT_SUPPORTED = 221;
    public static final int SESSION_ALREADY_EXISTS = 222;

    public static final int TRANSACTION_ALREADY_EXISTS_SUCCESS = 300;
    public static final int TRANSACTION_ALREADY_EXISTS_FAILED = 301;
    public static final int TRANSACTION_FAILED = 302;
    public static final int INACTIVE_PIN = 303;
    public static final int INVALID_PIN = 304;
    public static final int EXPIRED_PIN = 305;
    public static final int LOST_OR_STOLEN_PIN = 306;
    public static final int ALREADY_USED_PIN = 307;
    public static final int VOUCHER_INFO_UNAVAILABLE = 308;
    public static final int INSUFFICIENT_FUNDS = 309;
    public static final int NO_RECORD = 310;
    public static final int INVALID_PAYMENT_PARTNER = 311;
    public static final int INVALID_PROVIDER = 312;
    public static final int RECORD_ALREADY_EXISTS = 313;
    public static final int TRANSACTION_RECORD_EXPIRED = 314;
    public static final int RESOURCE_NOT_FOUND = 315;
    public static final int ERROR_READING_RESOURCE = 316;
    public static final int CONSTRAINT_VIOLATION = 317;
    public static final int INAVLID_IMAGE_FORMAT = 318;
    public static final int INVALID_FILE_FORMAT = 319;
    public static final int TRANSACTION_SUCCESSFUL = 320;
    public static final int TRANSACTION_STATUS_UNKNOWN = 321;
    public static final int TRANSACTION_PENDING = 322;
    

    public static final int REQUESTER_AUTH_ERROR = 901;
    public static final int SERVER_AUTH_ERROR = 902;
    public static final int INTERNAL_SYSTEM_ERROR = 903;
    public static final int SERVICE_ERROR = 905;
    public static final int DATABASE_ERROR = 906;
    public static final int FIILESYSTEM_ERROR = 907;

}
