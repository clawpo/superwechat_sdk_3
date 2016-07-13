package cn.ucai.superwechat.bean;

public class MemberUserAvatar extends UserAvatar {
	private Integer mmemberId;
	private Integer mmemberGroupId;
	private String mmemberGroupHxid;
	private Integer mmemberPermission;
	public MemberUserAvatar() {
		super();
	}

	public MemberUserAvatar(String muserName, String muserNick, Integer mavatarId, String mavatarPath,String mavatarSuffix,
			Integer mavatarType, String mavatarLastUpdateTime,Integer mmemberId, Integer mmemberGroupId, String mmemberGroupHxid,
			Integer mmemberPermission) {
		super(muserName, muserNick, mavatarId, mavatarPath,mavatarSuffix, mavatarType, mavatarLastUpdateTime);
		this.mmemberId = mmemberId;
		this.mmemberGroupId = mmemberGroupId;
		this.mmemberGroupHxid = mmemberGroupHxid;
		this.mmemberPermission = mmemberPermission;
	}

	public Integer getMMemberId() {
		return mmemberId;
	}
	public void setMMemberId(Integer mmemberId) {
		this.mmemberId = mmemberId;
	}
	public Integer getMMemberGroupId() {
		return mmemberGroupId;
	}
	public void setMMemberGroupId(Integer mmemberGroupId) {
		this.mmemberGroupId = mmemberGroupId;
	}
	public String getMMemberGroupHxid() {
		return mmemberGroupHxid;
	}
	public void setMMemberGroupHxid(String mmemberGroupHxid) {
		this.mmemberGroupHxid = mmemberGroupHxid;
	}
	public Integer getMMemberPermission() {
		return mmemberPermission;
	}
	public void setMMemberPermission(Integer mmemberPermission) {
		this.mmemberPermission = mmemberPermission;
	}
	@Override
	public String toString() {
		return "MemberUserAvatar [mmemberId=" + mmemberId + ", mmemberGroupId=" + mmemberGroupId + ", mmemberGroupHxid="
				+ mmemberGroupHxid + ", mmemberPermission=" + mmemberPermission + "]";
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MemberUserAvatar)) return false;
        if (!super.equals(o)) return false;

        MemberUserAvatar that = (MemberUserAvatar) o;

        if (!mmemberId.equals(that.mmemberId)) return false;
        if (!mmemberGroupId.equals(that.mmemberGroupId)) return false;
        return mmemberGroupHxid.equals(that.mmemberGroupHxid);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + mmemberId.hashCode();
        result = 31 * result + mmemberGroupId.hashCode();
        result = 31 * result + mmemberGroupHxid.hashCode();
        return result;
    }
}
