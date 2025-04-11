
package train.booking.train.booking.dto.response;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Objects;

public class Status implements Serializable{
    

    
    private Integer code;
    private String description;

    public Status() {
    }

    public Status(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    @Override
    public String toString() {
        return "Status{" + "code=" + code + ", description=" + description + '}';
    }
    
}
