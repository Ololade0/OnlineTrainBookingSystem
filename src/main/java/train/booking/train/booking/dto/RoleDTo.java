package train.booking.train.booking.dto;

import lombok.*;
import train.booking.train.booking.model.enums.RoleType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleDTo {
    private RoleType roleType;
    }

