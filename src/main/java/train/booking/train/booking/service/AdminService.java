package train.booking.train.booking.service;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.data.domain.Page;
import train.booking.train.booking.dto.FindAllByRolesDTO;
import train.booking.train.booking.dto.UserDTO;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.model.enums.RoleType;

public interface AdminService  {
    BaseResponse superAdminSignUp(UserDTO userDTO) throws UnirestException;

    Page<FindAllByRolesDTO> findAllByRole(RoleType roleType, int page, int size);



}
