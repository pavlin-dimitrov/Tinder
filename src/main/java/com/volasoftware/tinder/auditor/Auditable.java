package com.volasoftware.tinder.auditor;

import static javax.persistence.TemporalType.TIMESTAMP;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@ToString
@NoArgsConstructor
@Getter
@Setter
@MappedSuperclass // This means that this class has not table created for it
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable<T> {

  @CreatedBy
 // @Column(updatable = false)
  protected T createdBy;

  @CreatedDate
  @Temporal(TIMESTAMP)
 // @Column(updatable = false)
  protected Date createdAt;

  @LastModifiedBy
  protected T lastModifiedBy;

  @LastModifiedDate
  @Temporal(TIMESTAMP)
  protected Date lastModifiedAt;

  public Auditable(Date createdAt, T createdBy, T lastModifiedBy, Date lastModifiedAt) {
    this.createdAt = createdAt;
    this.createdBy = createdBy;
    this.lastModifiedBy = lastModifiedBy;
    this.lastModifiedAt = lastModifiedAt;
  }
}
