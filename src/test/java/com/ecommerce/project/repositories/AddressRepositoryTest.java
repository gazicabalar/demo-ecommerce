package com.ecommerce.project.repositories;

import com.ecommerce.project.model.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AddressRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private AddressRepository addressRepository;

    private Address address1;
    private Address address2;

    @BeforeEach
    void setUp(){
        address1 = new Address();
        address1.setStreet("Mendil Sokak");
        address1.setCity("Ankara");
        address1.setState("Etlik");
        address1.setCountry("Türkiye");
        address1.setBuildingName("Fatih Apartmanı");
        address1.setPostCode("06400");

        address2 = new Address();
        address2.setStreet("Şahin Sokak");
        address2.setCity("İstanbul");
        address2.setState("Kartal");
        address2.setCountry("Türkiye");
        address2.setBuildingName("Doğan Apartmanı");
        address2.setPostCode("34642");
    }

    @Test
    @DisplayName("Adres kaydedildiğinde ID atanmalı ve Adres kaydedilmeli.")
    void givenAddress_thenSave_thenAddressIsPersisted(){
        Address savedAddress = addressRepository.save(address1);
        Address savedAddress2 = addressRepository.save(address2);
        assertThat(savedAddress.getAddressId()).isNotNull();
        assertThat(savedAddress.getStreet()).isEqualTo("Mendil Sokak");
        assertThat(savedAddress.getCity()).isEqualTo("Ankara");
        assertThat(savedAddress.getState()).isEqualTo("Etlik");
        assertThat(savedAddress.getCountry()).isEqualTo("Türkiye");
        assertThat(savedAddress.getBuildingName()).isEqualTo("Fatih Apartmanı");
        assertThat(savedAddress.getPostCode()).isEqualTo("06400");

        assertThat(savedAddress2.getAddressId()).isNotNull();
        assertThat(savedAddress2.getStreet()).isEqualTo("Şahin Sokak");
        assertThat(savedAddress2.getCity()).isEqualTo("İstanbul");
        assertThat(savedAddress2.getState()).isEqualTo("Kartal");
        assertThat(savedAddress2.getCountry()).isEqualTo("Türkiye");
        assertThat(savedAddress2.getBuildingName()).isEqualTo("Doğan Apartmanı");
        assertThat(savedAddress2.getPostCode()).isEqualTo("34642");
    }

    @Test
    @DisplayName("Adres ID bulunabilmeli.")
    void givenAddress_thenFindById_thenAddressIsFound(){
        Address persistedAddress = testEntityManager.persistAndFlush(address1);
        Optional<Address> foundAddress =addressRepository.findById(persistedAddress.getAddressId());
        assertThat(foundAddress.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Tüm adresler listelenebilmeli.")
    void givenTwoAddresses_whenFindAll_thenReturnBothAddresses(){
        testEntityManager.persistAndFlush(address1);
        testEntityManager.persistAndFlush(address2);
        List<Address> addresses = addressRepository.findAll();
        assertThat(addresses).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Şehir adı ile adres bulunabilmeli.")
    void givenAddress_whenFindByCity_thenAddressIsFound(){
        testEntityManager.persistAndFlush(address1);
        testEntityManager.persistAndFlush(address2);

        List<Address> result = addressRepository.findByCity("Ankara");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCity()).isEqualTo("Ankara");
    }

    @Test
    @DisplayName("Adres güncellendiğinde şehir adı güncellenmeli.")
    void givenPersistedAddress_whenUpdateCity_thenAddressIsUpdated(){
        Address persistedAddress =testEntityManager.persistAndFlush(address1);
        persistedAddress.setCity("İzmir");
        addressRepository.save(persistedAddress);

        testEntityManager.flush();
        testEntityManager.clear();

        Optional<Address> foundAddress = addressRepository.findById(persistedAddress.getAddressId());
        assertThat(foundAddress.isPresent()).isTrue();
        assertThat(foundAddress.get().getCity()).isEqualTo("İzmir");
    }

    @Test
    @DisplayName("Adres silindiğinde boş dönmeli.")
    void givenAddress_thenDelete_thenAddressIsDeleted(){
        Address savedAddress = addressRepository.save(address1);
        addressRepository.delete(savedAddress);
        assertThat(addressRepository.findById(savedAddress.getAddressId())).isEmpty();
    }

}