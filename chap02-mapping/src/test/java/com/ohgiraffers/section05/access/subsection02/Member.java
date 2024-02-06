package com.ohgiraffers.section05.access.subsection02;

import jakarta.persistence.*;

/*
*   필드 접근이 기본 값이므로 해당 설정은 제거해도 동일하게 동작한다.
*   또한 필드 레벨과 프로퍼티 레벨이 모두 선언하면 프로퍼티 레벨을 우선으로 사용한다.
*
* */
@Entity(name = "member_section05_subsection02")
@Table(name = "tbl_member_section05_subsection02")
@Access(AccessType.PROPERTY)
public class Member {

    @Column(name = "member_no")
    private int memberNo;

    @Column(name = "member_id")
    private String memberId;

    @Column(name = "member_pwd")
    private String memberPwd;

    @Column(name = "nickname")
    private String nickName;

    @Id
    public int getMemberNo() {
        System.out.println("getMemberNo를 이용한 access 확인");
        return memberNo;
    }

    @Access(AccessType.PROPERTY)
    public String getMemberId() {
        System.out.println("getMemberId를 이용한 access 확인");
        return memberId;
    }


    public Member() {
    }

    public Member(int memberNo, String memberId, String memberPwd, String nickName) {
        this.memberNo = memberNo;
        this.memberId = memberId;
        this.memberPwd = memberPwd;
        this.nickName = nickName;
    }


    @Override
    public String toString() {
        return "Member{" +
                "memberNo=" + memberNo +
                ", memberId='" + memberId + '\'' +
                ", memberPwd='" + memberPwd + '\'' +
                ", nickName='" + nickName + '\'' +
                '}';
    }


    public void setMemberNo(int memberNo) {
        this.memberNo = memberNo;
    }


    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberPwd() {
        return memberPwd;
    }

    public void setMemberPwd(String memberPwd) {
        this.memberPwd = memberPwd;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}