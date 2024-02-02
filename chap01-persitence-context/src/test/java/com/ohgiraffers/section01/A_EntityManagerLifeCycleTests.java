package com.ohgiraffers.section01;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

public class A_EntityManagerLifeCycleTests {

    /*
    *   엔티티 매니저 팩토리 (EntityManagerFactory) 란?
    *   엔티티 매니저를 생성할 수 있는 기능을 제공하는 팩토리 클래스이다.
    *   Thread-safe 하기  때문에 여러 스레드가 동시에 접근해도 안전하므로 서로 다른 스레드 간 공유해서 재사용한다.
    *   Thread-safe 한 기능을 요청 스코프마다 생성하기에는 비용 (시간, 메모리) 부담이 크므로
    *   Application 스코프와 동일하게 싱글톤으로 생성해서 관리하는 것이 효율적이다.
    *   따라서 데이터베이스를 사용하는 애플리케이션 당 한 개의 EntityManagerFactory를 생성한다.
    * 
    *   db와 커넥션 맺은 객체
    * */

    /*
    *   엔티티 매니저(Entity Manager) 란?
    *   엔티티 매니저는 엔티티를 저장하는 메모리상의 데이터베이슬르 관리하는 인스턴스이다.
    *   엔티티를 저장하고 , 수정 , 삭제, 조회 하는 등의 엔티티와 관련된 모든 일은 한다.
    *   엔티티 매니저는 thread-safe 하지 않기 때문에 동시성 문제가 발생할 수 있다.
    *
    *   따라서 스레드 간 공유하지 않고 , Web 의 경우 일반적으로 request scope와 일치시킨다.
    * 
    *   db에 명령을 내리기 위한 인터페이스 - 리모콘?
    * 
    * 
    *
    *   영속성 컨텍스트 (persistence context) 란?
    *   엔티티 매니저를 통해 엔티티를 저장하거나 조회하면 엔티티 매니저는 영속성 컨텍스트에 엔티티를 보관하고 관리한다.
    *   영속성 컨텍스트는 엔티티를 key-value 방식으로 저장하는 저장소이다.
    *   영속성 컨텍스트는 엔티티 매니저를 생성할 때 하나 만들어진다.
    *   그리고 엔티티 매니저를 통해서 영속성 컨텍스트에 접근할 수 있고, 또 관리할 수 있다.
    *   
    *   최신화된 저장소  -- 엔티티매니저가 명령 내린후 가져온 내용을 저장하는 곳.
    *
    * jdbc // servlet 파트 참고 하면 이해하기 좋음.
    * */

    private static EntityManagerFactory entityManagerFactory; // 하나만 존재해야 하기에 static

    private EntityManager entityManager;  // 요청시마다 생성을 위함.

    @BeforeAll
    public static void initFactory(){
        entityManagerFactory = Persistence.createEntityManagerFactory("jpatest");  //  영속성 컨텍스트 생성
    }

    @BeforeEach
    public void initManager(){
        entityManager = entityManagerFactory.createEntityManager();   // 조작할 수 있는 인터페이스 생성
    }

    @Test
    public void 엔터티_매니저_팩토리와_엔터티_매니저_생명주기_확인1(){
        System.out.println("entityManagerFactory.hashCode : " + entityManagerFactory.hashCode());  // 매번 같음
        System.out.println("entityManager.hashCode : " + entityManager.hashCode());  // 매번 다름
    }
    @Test
    public void 엔터티_매니저_팩토리와_엔터티_매니저_생명주기_확인2(){
        System.out.println("entityManagerFactory.hashCode : " + entityManagerFactory.hashCode());
        System.out.println("entityManager.hashCode : " + entityManager.hashCode());
    }

    @AfterAll
    public static void closeFactory(){
        entityManagerFactory.close();
    }
    @AfterEach
    public void closeManager(){
        entityManager.close();
    }

}
