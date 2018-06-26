package net.lc4ever.framework.state.instance;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.lc4ever.framework.domain.BaseEntity;

@Entity
@Table(name="ADMIN_USER_EDIT_HISTORY")
public class AdminUserEditHistory implements BaseEntity<Long>{

	private Long id;
	
	/**
	 * 被编辑的操作员和被编辑的角色的id
	 */
	private String oidAdminUserId;
	
	/**
	 * 操作员编辑和角色编辑历史内容
	 */
	private String roleEditContent;
	
	/**
	 * 新增时间
	 */
	private Date insDate;
	
	/**
	 * 操作员或角色的修改者
	 */
	private String updateUser;
	
	/**
	 * 操作员编辑历史：1，角色编辑历史：3,这张表同时保存操作员和角色的编辑历史
	 */
	private String operationMenuFlag;
	
	
	@Override
	@Id
	@Column(name="ID")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seq_admin_history")
	@SequenceGenerator(name="seq_admin_history",sequenceName="SEQ_ADMIN_USER_EDIT_HISTORY")
	public Long getId() {
		return id;
	}
	
	@Override
	public void setId(Long id) {
		this.id = id;
	}
	
	
	@Column(name="OID_ADMIN_USER_ID", length=32)
	public String getOidAdminUserId() {
		return oidAdminUserId;
	}
	public void setOidAdminUserId(String oidAdminUserId) {
		this.oidAdminUserId = oidAdminUserId;
	}
	
	@Column(name="ROLE_EDIT_CONTENT")
	@Lob
	public String getRoleEditContent() {
		return roleEditContent;
	}
	public void setRoleEditContent(String roleEditContent) {
		this.roleEditContent = roleEditContent;
	}
	
	@Column(name="INS_DATE")
	public Date getInsDate() {
		return insDate;
	}
	public void setInsDate(Date insDate) {
		this.insDate = insDate;
	}
	
	@Column(name="UPDATE_USER", length=32)
	public String getUpdateUser() {
		return updateUser;
	}
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}
	
	@Column(name="OPERATION_MENU_FLAG", length=1)
	public String getOperationMenuFlag() {
		return operationMenuFlag;
	}
	public void setOperationMenuFlag(String operationMenuFlag) {
		this.operationMenuFlag = operationMenuFlag;
	}
	
}
