package com.ohgiraffers.section03.bidirection;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;


/*
*   양방향 매핑에서 어느 한 쪽이 연관 관계의 주인이 되면, 주인이 아닌 쪽에서는 속성을 지정해 주어야 한다.
*   이 때, 연관 관계의 주인이 아닌 객체 MappedBy 를 써서 연관 관계 주인 객체의 필드명을 매핑 시켜 놓으면 양방향 관계를 적용할 수 있다.
* 
* */
@Entity(name = "bidirection_category")
@Table(name = "tbl_category")
public class Category {

    @Id
    @Column(name = "category_code")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int categoryCode;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "ref_category_code")
    private Integer refCategoryCode;

/*    @OneToMany(mappedBy = "categoryCode") // 연관관계를 맺고 있는 상대편 클래스의 필드 ( 주인이 아닌 쪽에 mappedBy 작성 )  조회만 가능
    private List<Menu> menuList = new ArrayList<>();  // 조회를 용도로 사용*/

    @OneToMany(mappedBy = "categoryCode", cascade = CascadeType.PERSIST)
    private List<Menu> menuList = new ArrayList<>();  // 조회를 용도로 사용

    // db 제약조건에 위배되지만 자바 관점에서는 Menu가 들어가야 함.

    public Category() {
    }

    public Category(int categoryCode, String categoryName, Integer refCategoryCode) {
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
        this.refCategoryCode = refCategoryCode;
    }

    public int getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(int categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getRefCategoryCode() {
        return refCategoryCode;
    }

    public void setRefCategoryCode(Integer refCategoryCode) {
        this.refCategoryCode = refCategoryCode;
    }

    public List<Menu> getMenuList() {
        return menuList;
    }

    public void setMenuList(List<Menu> menuList) {

        List<Menu> list = new ArrayList<>();
        for (Menu m:menuList) {
            m.setCategory(this);
            list.add(m);
        }
        this.menuList = list;
    }

    @Override
    public String toString() {
        return "CategoryAndMenu{" +
                "categoryCode=" + categoryCode +
                ", categoryName='" + categoryName + '\'' +
                ", refCategoryCode=" + refCategoryCode +
                ", menuList=" + menuList +
                '}';
    }
}
