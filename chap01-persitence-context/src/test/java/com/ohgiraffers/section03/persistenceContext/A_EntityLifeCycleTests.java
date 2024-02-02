package com.ohgiraffers.section03.persistenceContext;


import jakarta.persistence.*;
import org.junit.jupiter.api.*;

public class A_EntityLifeCycleTests {
    private static EntityManagerFactory entityManagerFactory;

    private EntityManager entityManager;

    @BeforeAll
    public static void initFactory(){
        entityManagerFactory = Persistence.createEntityManagerFactory("jpatest");
    }

    @BeforeEach
    public void initManager(){
        entityManager = entityManagerFactory.createEntityManager();
    }

    @AfterAll
    static void closeFactory(){
        entityManagerFactory.close();
    }
    @AfterEach
    void closeManager(){
        entityManager.close();
    }

    /*
    *   영속성 컨텍스트는 엔티티 매니저가 엔티티 객체를 저장하는 공간으로 엔티티 객체를 보관하고 정리한다.
    *   엔티티 매니저가 생성 될 때 하나의 영속성 컨텍스트가 만들어 진다.
    * 
    *   엔티티의 생명주기
    *   비영속, 영속, 준영속, 삭제 상태
    * */

    @Test
    public void 비영속_테스트(){

        //given
        Menu foundMenu = entityManager.find(Menu.class, 11);

        // 객체만 생성하면 영속성 컨텍스트나 데이터베이스와 관련 없는 비영속 상태이다.
        Menu newMenu = new Menu();
        newMenu.setMenuCode(foundMenu.getMenuCode());
        newMenu.setMenuName(foundMenu.getMenuName());
        newMenu.setMenuPrice(foundMenu.getMenuPrice());
        newMenu.setCategoryCode(foundMenu.getCategoryCode());
        newMenu.setOrderableStatus(foundMenu.getOrderableStatus());


        //when  객체간의 비교. 주소값을 비교함
        boolean isTrue = (foundMenu == newMenu);



        //then
        Assertions.assertFalse(isTrue);

    }

    @Test
    void 영속성_연속_조회_테스트(){
        /*
        *   엔티티 매니저가 영속성 컨텍스트에 엔티티 객체를 저장(persist) 하면 영속성 컨텍스트가 엔티티 객체를 관리하게 되고
        *   이를 영속 상태라고 한다. Find(), jpql 을 사용한 조회도 영속 상태가 된다.
        * */

        // given
        Menu foundMenu1 = entityManager.find(Menu.class, 11);  // 영속화시킴
        Menu foundMenu2 = entityManager.find(Menu.class, 11);  // 다시 db로 조회할필요 X

        // when
        boolean isTrue = (foundMenu1 == foundMenu2);

        // then
        Assertions.assertTrue(isTrue);
    }

    @Test
    void 영속성_객체_추가_테스트(){
        
        // Menu Entity의 @GeneratedValue(strategy=GenerationType.IDETITY) 설정을 잠시 주석 하고 테스트 수행

        // given
        Menu menuToRegist = new Menu();
        menuToRegist.setMenuCode(500);
        menuToRegist.setMenuName("수박죽");
        menuToRegist.setMenuPrice(10000);
        menuToRegist.setCategoryCode(1);
        menuToRegist.setOrderableStatus("Y");

        //when
        entityManager.persist(menuToRegist);  // 영속성 컨텍스트에 새로운 객체를 추가함. ( db에 반영은 X )
        Menu foundMenu = entityManager.find(Menu.class, 500);  // 영속성컨텍스트에 들어있어서 굳이 db를 조회해 꺼내오지 않음
        boolean isTrue = (menuToRegist == foundMenu);

        Assertions.assertTrue(isTrue);

    }

    @Test
    void 준영속성_detach_테스트(){

        // given
        Menu foundMenu = entityManager.find(Menu.class, 11);
        Menu foundMenu1 = entityManager.find(Menu.class, 12);

        /*
        *   영속성 컨텍스트가 관리하던 엔티티 객체를 관리하지 않는 상태가 된다면 준영속 상태가 된다.
        *   그 중 Detach 는 특정 엔티티만 준영속 상태로 만든다.
        * */

        // when
        entityManager.detach(foundMenu1);  // entityManager 에서 foundMenu1을 뺌. 준영속 (잠시 뺌 다시 넣을 수 있음)

        foundMenu.setMenuPrice(5000);
        foundMenu1.setMenuPrice(5000);

        Assertions.assertEquals(5000, entityManager.find(Menu.class, 11).getMenuPrice());  // 영속성 컨텍스트에 존재 - 요청 X

        entityManager.merge(foundMenu1);  // 준영속 상태 다시 영속 상태로 변경 명령어

        Assertions.assertEquals(5000, entityManager.find(Menu.class, 12).getMenuPrice());  // 존재하지 않음 - 새로 요청
    }

    @Test
    void 준영속성_clear_테스트(){

        //given
        Menu foundMenu1 = entityManager.find(Menu.class, 11);
        Menu foundMenu2 = entityManager.find(Menu.class, 12);

        // when
        entityManager.clear();  //영속성 컨텍스트 초기화

        foundMenu2.setMenuPrice(5000);
        foundMenu1.setMenuPrice(5000);

        Assertions.assertNotEquals(5000, entityManager.find(Menu.class, 11).getMenuPrice());
        Assertions.assertNotEquals(5000, entityManager.find(Menu.class, 12).getMenuPrice());

    }

    @Test
    void close_테스트(){

        //given
        Menu foundMenu1 = entityManager.find(Menu.class, 11);
        Menu foundMenu2 = entityManager.find(Menu.class, 12);

        // when
        entityManager.close();

        foundMenu2.setMenuPrice(5000);
        foundMenu1.setMenuPrice(5000);

        //then
        // 영속성 컨텍스트를 닫았기 떄문에 다시 만들기 전에는 사용할 수 없다.
        Assertions.assertEquals(5000, entityManager.find(Menu.class,11).getMenuPrice());
        Assertions.assertEquals(5000, entityManager.find(Menu.class,12).getMenuPrice());
    }

    @Test
    public void 삭제_remove_테스트(){

        /*
        *   remove : 엔티티를 영속성 컨텍스트 및 데이터베이스에서 삭제한다.
        *   단, 트랜젝션을 제어하지 않으면 영구 반영되지는 않는다.
        *   트랜잭션을 커밋하는 순간 영속성 컨텍스트에서 관리하는 엔터티 객체가 데이터베이스에 반영되게 한다. (flush 라고 함)
        *   Flush : 영속성 컨텍스트의 변경 내용을 데이터베이스에 동기화 하는 작업(등록,수정,삭제한 엔티티를 데이터베이스에 반영)
        * */

        // given
        Menu foundMenu = entityManager.find(Menu.class, 2);

        //when
        entityManager.remove(foundMenu);  // 2번은 지울걸로 예정, 예정된 삭제이기에 새로 요청하지 않고 null 반환
        Menu refoundMenu = entityManager.find(Menu.class, 2);

        Assertions.assertEquals(2,foundMenu.getMenuCode());
        Assertions.assertEquals(null, refoundMenu);
    }

    /*
    *   병합(merge) : 파라미터로 넘어온 준영속 엔티티 개체의 식별 값으로 1차 캐시에서 엔티티 객체를 조회한다.
    *   만약 1차 캐시에 엔티티가 없으면 데이터베이스에서 엔티티를 조회하고 1차 캐시에 저정한다.
    *   조회한 영속 엔티티 객체에 준영속 상태의 엔티티 객체의 값을 병합한 뒤 영속 엔티티 객체를 반환한다.
    *   혹은 조회할 수 없는 데이터의 경우 새로 생성해서 병합한다. (save or update)
    * */

    @Test
    void 병합_merge_수정_테스트(){
        //given
        Menu menuToDetach = entityManager.find(Menu.class, 3);
        entityManager.detach(menuToDetach);

        //when
        menuToDetach.setMenuName("수박죽");
        Menu refoundMenu = entityManager.find(Menu.class, 3);  //remove는 null을 반환하지만 준속성상태는 다시 db에서 조회해옴

        // 준영속 엔티티와 영속 엔티티의 해쉬코드는 다른 상태다..
        System.out.println(menuToDetach.hashCode());
        System.out.println(refoundMenu.hashCode());

        entityManager.merge(menuToDetach);
        //then
        Menu mergedMenu = entityManager.find(Menu.class, 3);
        Assertions.assertEquals("수박죽",mergedMenu.getMenuName());
    }

    @Test
    void 병합_merge_삽입_테스트(){
        //given
        Menu menuToDetach = entityManager.find(Menu.class,3);
        entityManager.detach(menuToDetach);  // 준영속화

        //when
        menuToDetach.setMenuCode(999);  // id를 변경하면 식별하지 못함 -> 다시 병합 하지 못함
        menuToDetach.setMenuName("수박죽");

        entityManager.merge(menuToDetach);  // 다시 영속화 시도 ->실패 (id 다름) -> db에서 확인

        //then
        Menu mergedMenu = entityManager.find(Menu.class,999);  // 찾지 못함. null 값 보냄

       //  Assertions.assertEquals("수박죽", mergedMenu.getMenuName()); // 불일치
        Assertions.assertNull(mergedMenu);
    }
    @Test
    void 병합_merge_삽입_테스트2(){
        //given
        Menu menuToDetach = entityManager.find(Menu.class,3);
        entityManager.detach(menuToDetach);  // 준영속화

        //when
        menuToDetach.setMenuCode(6760);  // id를 변경하면 식별하지 못함 -> 다시 병합 하지 못함
        menuToDetach.setMenuName("수박죽");

        entityManager.merge(menuToDetach);  // 다시 영속화 시도 ->실패 (id 다름) -> db에서 확인 db에 있는 값을 다시 넣어 merge  해줌 (여기서는 이름 변경)

        //then
        Menu mergedMenu = entityManager.find(Menu.class,6760);  // 찾음

          Assertions.assertEquals("수박죽", mergedMenu.getMenuName()); // 일치
    }
    @Test
    void 병합_merge_삽입_테스트3(){
        //given
        Menu menuToDetach = entityManager.find(Menu.class,3);
        entityManager.detach(menuToDetach);  // 준영속화

        //when
        menuToDetach.setMenuCode(999);
        menuToDetach.setMenuName("수박죽");
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.merge(menuToDetach);
        transaction.commit();   // db에 반영했을때 없는 값이면 새로 insert 쿼리를 날림

        //then
        Menu mergedMenu = entityManager.find(Menu.class,999);

        Assertions.assertEquals("수박죽", mergedMenu.getMenuName());
    }
}
