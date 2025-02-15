package train.booking.train.booking.model;


import jakarta.persistence.*;
import lombok.*;
import train.booking.train.booking.model.enums.RoleType;

@Setter
@Getter
@Builder
@ToString
@Entity(name = "role")
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

  @Enumerated(EnumType.STRING)
    private RoleType roleType;

    public Role(RoleType roleType) {
        this.roleType = roleType;

    }


    @Override
    public String toString() {
        return "Role{" +
                "roleStatus=" + roleType +
                '}';
    }



}
