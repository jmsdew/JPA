package com.ohgiraffers.section05.access.subsection02;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

public class PropertyAccessTests {
    private static EntityManagerFactory entityManagerFactory;  // 싱글톤 리소스를 줄이기 위해
    private EntityManager entityManager;

    @BeforeAll
    public static void initFactory() {
        entityManagerFactory = Persistence.createEntityManagerFactory("jpatest");
    }

    @BeforeEach
    public void initManager() {
        entityManager = entityManagerFactory.createEntityManager();
    }

    @AfterAll
    public static void closeFactory() {
        entityManagerFactory.close();
    }

    @AfterEach
    public void closeManager() {
        entityManager.close();
    }

    @Test
    void 필드_접근_테스트(){

        //given
        Member member = new Member();
        member.setMemberNo(1);
        member.setMemberId("user01");
        member.setMemberPwd("pass01");
        member.setNickName("홍길동");

        //when
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();
        entityManager.persist(member);
        entityTransaction.commit();

        //then
        String jpql = "SELECT a.memberId FROM member_section05_subsection02 a WHERE a.memberNo = 1";
        String registedNickname = entityManager.createQuery(jpql, String.class).getSingleResult();
        System.out.println(registedNickname);
        Assertions.assertEquals("user01", registedNickname);
    }
}
