
package train.booking.train.booking.dto.response;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Objects;

public class BaseResponse<E> implements Serializable {



  private Status status;

  private E entity;

  public BaseResponse() {
    this.status = new Status(ResponseCodes.REQUEST_SUCCESSFUL, "Request Successful");
  }

  public BaseResponse(Integer rc, String description) {
    this.status = new Status(rc, description);
  }

  public BaseResponse(Status status, E entity) {
    this.status = status;
    this.entity = entity;
  }

  public BaseResponse(Integer rc, String description, E entity) {
    this.status = new Status(rc, description);
    this.entity = entity;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public E getEntity() {
    return entity;
  }

  public void setEntity(E entity) {
    this.entity = entity;
  }

//  @Override
//  public int hashCode() {
//    int hash = 3;
//    hash = 17 * hash + Objects.hashCode(this.status);
//    hash = 17 * hash + Objects.hashCode(this.entity);
//    return hash;
//  }
//
//  @Override
//  public boolean equals(Object obj) {
//    if (obj == null) {
//      return false;
//    }
//    if (getClass() != obj.getClass()) {
//      return false;
//    }
//    final BaseResponse<?> other = (BaseResponse<?>) obj;
//    if (!Objects.equals(this.status, other.status)) {
//      return false;
//    }
//    if (!Objects.equals(this.entity, other.entity)) {
//      return false;
//    }
//    return true;
//  }

  @Override
  public String toString() {
    return "BaseResponse{" + "status=" + status + ", entity=" + entity + '}';
  }


}
