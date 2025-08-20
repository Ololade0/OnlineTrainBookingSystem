package train.booking.train.booking.service;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.data.domain.Page;
import train.booking.train.booking.dto.FindAllByRolesDTO;
import train.booking.train.booking.dto.UserDTO;
import train.booking.train.booking.dto.response.BaseResponse;
import train.booking.train.booking.model.enums.IdentificationType;
import train.booking.train.booking.model.enums.RoleType;

import javax.management.relation.RoleNotFoundException;
import java.util.List;

public interface AdminService  {
    BaseResponse superAdminSignUp(UserDTO userDTO) throws UnirestException, RoleNotFoundException;

    Page<FindAllByRolesDTO> findAllByRole(RoleType roleType, int page, int size);


    List<IdentificationType> getAllIdenticationTypes();
}
