package cn.ieasy.framework.common.web.audit;

import java.util.Date;

public interface Auditable {

	String getCreatedBy();

	Date getCreatedDate();

	String getLastModifiedBy();

	Date getLastModifiedDate();
}
