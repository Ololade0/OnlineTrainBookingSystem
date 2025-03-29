/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package train.booking.train.booking.dto.response;


/**
 *
 * @author prodigy4440
 */
public class ResponseUtil {

  public static BaseResponse success(String description, Object entity) {
    BaseResponse baseResponse = new BaseResponse(ResponseCodes.REQUEST_SUCCESSFUL, description);
    baseResponse.setEntity(entity);
    return baseResponse;
  }

  public static BaseResponse response(Integer rc, String description, Object entity) {
    BaseResponse baseResponse = new BaseResponse(rc, description);
    baseResponse.setEntity(entity);
    return baseResponse;
  }

  public static BaseResponse invalidOrNullInput(String description) {
    BaseResponse baseResponse = new BaseResponse(ResponseCodes.BAD_INPUT_PARAM, description);
    return baseResponse;
  }

  public static BaseResponse inputAlreadyExist(String description) {
    BaseResponse baseResponse = new BaseResponse(ResponseCodes.RECORD_ALREADY_EXISTS, description);
    return baseResponse;
  }
  public static BaseResponse DoesNotExist() {
    return new BaseResponse(ResponseCodes.ALREADY_EXISTS, "User with the supplied user name or email exist");
  }
}
