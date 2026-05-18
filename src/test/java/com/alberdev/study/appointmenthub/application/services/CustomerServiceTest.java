package com.alberdev.study.appointmenthub.application.services;

import com.alberdev.study.appointmenthub.domain.entities.Customer;
import com.alberdev.study.appointmenthub.domain.exceptions.DomainException;
import com.alberdev.study.appointmenthub.domain.exceptions.ResourceAlreadyExistsException;
import com.alberdev.study.appointmenthub.domain.exceptions.ResourceNotFoundException;
import com.alberdev.study.appointmenthub.infrastructure.repositories.CustomerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.alberdev.study.appointmenthub.domain.entities.CustomerTestData.createValidCustomer;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    // =========================
    // Create customer
    // =========================

    @Test
    @DisplayName("Deve criar cliente quando e-mail ainda nao estiver cadastrado")
    void shouldCreateCustomerWhenEmailDoesNotExist() {
        // 1. Arrange: criar um Customer válido
        Customer customer = createValidCustomer();

        // 2. Arrange: ENSINAR o Mockito a devolver vazio (não existe e-mail)
        // Em vez de chamar o método, usamos o "when"
        Mockito.when(customerRepository.findByEmail(customer.getEmail()))
                .thenReturn(Optional.empty());

        // 3. Arrange: ENSINAR o Mockito a devolver o cliente quando salvar
        Mockito.when(customerRepository.save(Mockito.any(Customer.class)))
                .thenReturn(customer);

        // 4. Act: Aqui sim você CHAMA a sua Service de verdade
        Customer savedCustomer = customerService.create(customer);

        // 5. Assert: Validar se o que a Service devolveu está correto
        assertNotNull(savedCustomer);
        assertEquals(customer.getEmail(), savedCustomer.getEmail());

        // 6. Verify: Garantir que a Service realmente usou o repositório
        Mockito.verify(customerRepository, Mockito.times(1)).save(customer);
    }

    @Test
    @DisplayName("Deve lancar excecao ao criar cliente com e-mail ja cadastrado")
    void shouldThrowExceptionWhenCreatingCustomerWithExistingEmail() {
        // 1. Arrange: criar o cliente que tentaremos cadastrar
        Customer customer = createValidCustomer();

        // 2. Arrange: SIMULAR que o e-mail JA EXISTE (retornando o próprio customer)
        Mockito.when(customerRepository.findByEmail(customer.getEmail()))
                .thenReturn(Optional.of(customer));

        // 3. Act & Assert: Verificamos se a exceção correta é lançada
        assertThrows(ResourceAlreadyExistsException.class, () -> {
            customerService.create(customer);
        });

        // 4. Verify: Garantimos que o repository.save NUNCA foi executado
        // Se o save foi chamado, o teste falha.
        Mockito.verify(customerRepository, Mockito.never()).save(Mockito.any(Customer.class));
    }

    // =========================
    // Find customer
    // =========================

    @Test
    @DisplayName("Deve buscar cliente por id quando existir")
    void shouldFindCustomerByIdWhenExists() {
        // 1. Arrange: criar Customer valido e definir um id
        Customer customer = createValidCustomer();
        customer.setId(1L);
        // 2. Arrange: simular repository.findById retornando Optional com Customer
        Mockito.when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        // 3. Act: chamar customerService.findById(id)
        var foundedCustomer = customerService.findById(customer.getId());
        // 4. Assert: validar que retornou o cliente esperado
        assertEquals(customer.getId(), foundedCustomer.getId());
        // 5. Verify: verificar que findById foi chamado
        Mockito.verify(customerRepository, Mockito.times(1)).findById(customer.getId());
    }

    @Test
    @DisplayName("Deve lancar excecao ao buscar cliente inexistente por id")
    void shouldThrowExceptionWhenCustomerByIdDoesNotExist() {
        // 1. Arrange: escolher um id inexistente
        Customer customer = createValidCustomer();
        customer.setId(1L);
        // 2. Arrange: simular repository.findById retornando Optional.empty()
        Mockito.when(customerRepository.findById(customer.getId())).thenReturn(Optional.empty());
        // 3. Act & Assert: chamar customerService.findById(id) e esperar ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> {
            customerService.findById(customer.getId());
        });
        // 4. Verify: verificar que findById foi chamado
        Mockito.verify(customerRepository, Mockito.times(1)).findById(customer.getId());
    }

    @Test
    @DisplayName("Deve listar todos os clientes")
    void shouldFindAllCustomers() {
        // 1. Arrange: criar uma lista com clientes validos
        List<Customer> customers = new ArrayList<>();
        customers.add(createValidCustomer());
        // 2. Arrange: simular repository.findAll retornando essa lista
        Mockito.when(customerRepository.findAll()).thenReturn(customers);
        // 3. Act: chamar customerService.findAll()
        List<Customer> result = customerService.findAll();
        // 4. Assert: validar se o que voltou está correto
        assertNotNull(result);
        assertEquals(1, result.size()); // Agora o tamanho será 1, conforme planejado
        assertEquals(customers.get(0).getName(), result.get(0).getName());

        // 5. Verify: garantir que o repositório foi consultado
        Mockito.verify(customerRepository, Mockito.times(1)).findAll();
    }

    // =========================
    // Update customer
    // =========================

    @Test
    @DisplayName("Deve atualizar cliente quando dados forem validos")
    void shouldUpdateCustomerWhenDataIsValid() {
        // 1. Arrange: criar o objeto que simula o que está no banco atualmente
        Customer customerOriginal = createValidCustomer();
        customerOriginal.setId(1L);

        // 2. Arrange: dados que o usuário enviou para atualizar
        Customer updateData = new Customer();
        updateData.setName("Maria Oliveira");
        updateData.setEmail("maria@email.com");
        updateData.setPhone("11888888888");

        // 3. Arrange: Ensinar o Mockito
        Mockito.when(customerRepository.findById(customerOriginal.getId()))
                .thenReturn(Optional.of(customerOriginal));

        Mockito.when(customerRepository.findByEmail("maria@email.com"))
                .thenReturn(Optional.empty());

        // Configuramos o Mockito para retornar a mesma instância que a Service irá manipular.
//      Assim, qualquer alteração feita pela Service será refletida no retorno do save.
        Mockito.when(customerRepository.save(Mockito.any(Customer.class)))
                .thenReturn(customerOriginal);

        // 4. Act
        var result = customerService.update(customerOriginal.getId(), updateData);

        // 5. Assert: Compare com os valores literais esperados
        assertNotNull(result);
        assertEquals("Maria Oliveira", result.getName());
        assertEquals("maria@email.com", result.getEmail());
        assertEquals("11888888888", result.getPhone());

        // 6. Verify: Verifique se salvou o objeto correto
        Mockito.verify(customerRepository, Mockito.times(1)).save(customerOriginal);
    }

    @Test
    @DisplayName("Deve manter dados antigos quando update receber campos nulos")
    void shouldKeepCurrentDataWhenUpdateFieldsAreNull() {
        // 1. Arrange: criar Customer existente com name, email e phone
        Customer customerOriginal = createValidCustomer();
        customerOriginal.setId(1L);

        // 2. Arrange: criar Customer update com campos nulos
        Customer updateData = new Customer();

        // 3. Arrange: simular repository.findById retornando o cliente existente
        Mockito.when(customerRepository.findById(customerOriginal.getId())).thenReturn(Optional.of(customerOriginal));
        // 4. Arrange: simular repository.save retornando o cliente preservado
        Mockito.when(customerRepository.save(Mockito.any(Customer.class)))
                .thenReturn(customerOriginal);
        // 5. Act: chamar customerService.update(id, customerUpdate)
        var result = customerService.update(customerOriginal.getId(), updateData);
        // 6. Assert: validar que name, email e phone originais foram preservados
        assertNotNull(result);
        assertEquals("Joao Silva", result.getName());
        assertEquals("joao@email.com", result.getEmail());
        assertEquals("11999999999", result.getPhone());

        // 7. Verify: Verifique se salvou o objeto correto
        Mockito.verify(customerRepository, Mockito.times(1)).save(customerOriginal);
    }

    @Test
    @DisplayName("Deve lancar excecao ao atualizar cliente inexistente")
    void shouldThrowExceptionWhenUpdatingCustomerThatDoesNotExist() {
        // 1. Arrange: escolher um id inexistente
        Customer customerOriginal = createValidCustomer();
        customerOriginal.setId(1L);
        // 2. Arrange: criar Customer update valido
        Customer updateData = new Customer();
        updateData.setName("Maria Oliveira");
        updateData.setEmail("maria@email.com");
        updateData.setPhone("11888888888");
        // 3. Arrange: simular repository.findById retornando Optional.empty()
       Mockito.when(customerRepository.findById(customerOriginal.getId())).thenReturn(Optional.empty());
        // 4. Act & Assert: chamar customerService.update(id, customerUpdate) e esperar ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> {
            customerService.update(customerOriginal.getId(), updateData);
        });

        // 5. Verify: verificar que save nunca foi chamado
        Mockito.verify(customerRepository, Mockito.never()).save(Mockito.any(Customer.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao atualizar e-mail para outro ja cadastrado")
    void shouldThrowExceptionWhenUpdatingEmailToAnotherExistingEmail() {
        // 1. Arrange: criar Customer atual com id 1
        Customer currentCustomer = createValidCustomer();
        currentCustomer.setId(1L);

        // 2. Arrange: criar Customer update tentando usar novo e-mail
        Customer updateData = new Customer();
        updateData.setEmail("maria@email.com");

        // 3. Arrange: criar outro Customer existente com id 2 e o mesmo e-mail novo
        Customer anotherCustomer = createValidCustomer();
        anotherCustomer.setId(2L);
        anotherCustomer.setName("Maria Oliveira");
        anotherCustomer.setEmail("maria@email.com");
        anotherCustomer.setPhone("11888888888");

        // 4. Arrange: simular repository.findById retornando o Customer atual
        Mockito.when(customerRepository.findById(currentCustomer.getId())).thenReturn(Optional.of(currentCustomer));
        // 5. Arrange: simular repository.findByEmail retornando o outro Customer
        Mockito.when(customerRepository.findByEmail("maria@email.com"))
                .thenReturn(Optional.of(anotherCustomer));
        // 6. Act & Assert: chamar customerService.update(id, customerUpdate) e esperar ResourceAlreadyExistsException
        assertThrows(ResourceAlreadyExistsException.class, () -> {
            customerService.update(currentCustomer.getId(), updateData);
        });
        // 7. Verify: verificar que save nunca foi chamado
        Mockito.verify(customerRepository, Mockito.never()).save(Mockito.any(Customer.class));
    }

    // =========================
    // Activate/deactivate customer
    // =========================

    @Test
    @DisplayName("Deve ativar cliente inativo")
    void shouldActivateInactiveCustomer() {
        // 1. Arrange: criar Customer inativo com id
        var inactiveCustomer = createValidCustomer();
        inactiveCustomer.setId(1L);
        inactiveCustomer.deactivate();

        // 2. Arrange: simular repository.findById retornando esse Customer
        Mockito.when(customerRepository.findById(inactiveCustomer.getId()))
                .thenReturn(Optional.of(inactiveCustomer));

        // 3. Act: chamar customerService.activate(id)
        customerService.activate(inactiveCustomer.getId());

        // 4. Assert: validar que customer.isActive() ficou true
        assertTrue(inactiveCustomer.isActive());

        // 5. Verify: verificar que save foi chamado
        Mockito.verify(customerRepository, Mockito.times(1)).save(inactiveCustomer);
    }

    @Test
    @DisplayName("Deve desativar cliente ativo")
    void shouldDeactivateActiveCustomer() {
        // 1. Arrange: criar Customer ativo com id
        var activeCustomer = createValidCustomer();
        activeCustomer.setId(1L);

        // 2. Arrange: simular repository.findById retornando esse Customer
        Mockito.when(customerRepository.findById(activeCustomer.getId()))
                .thenReturn(Optional.of(activeCustomer));

        // 3. Act: chamar customerService.deactivate(id)
        customerService.deactivate(activeCustomer.getId());

        // 4. Assert: validar que customer.isActive() ficou false
        assertFalse(activeCustomer.isActive());

        // 5. Verify: verificar que save foi chamado
        Mockito.verify(customerRepository, Mockito.times(1)).save(activeCustomer);
    }

    @Test
    @DisplayName("Deve lancar excecao ao ativar cliente ja ativo")
    void shouldThrowExceptionWhenActivatingAlreadyActiveCustomer() {
        // 1. Arrange: criar Customer ativo com id
        var activeCustomer = createValidCustomer();
        activeCustomer.setId(1L);

        // 2. Arrange: simular repository.findById retornando esse Customer
        Mockito.when(customerRepository.findById(activeCustomer.getId()))
                .thenReturn(Optional.of(activeCustomer));

        // 3. Act & Assert: chamar customerService.activate(id) e esperar DomainException
        assertThrows(DomainException.class, () -> {
            customerService.activate(activeCustomer.getId());
        });

        // 4. Verify: avaliar se save nao deve ser chamado quando a regra falha
        Mockito.verify(customerRepository, Mockito.never()).save(Mockito.any(Customer.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao desativar cliente ja inativo")
    void shouldThrowExceptionWhenDeactivatingAlreadyInactiveCustomer() {
        // 1. Arrange: criar Customer inativo com id
        var inactiveCustomer = createValidCustomer();
        inactiveCustomer.setId(1L);
        inactiveCustomer.deactivate();

        // 2. Arrange: simular repository.findById retornando esse Customer
        Mockito.when(customerRepository.findById(inactiveCustomer.getId()))
                .thenReturn(Optional.of(inactiveCustomer));

        // 3. Act & Assert: chamar customerService.deactivate(id) e esperar DomainException
        assertThrows(DomainException.class, () -> {
            customerService.deactivate(inactiveCustomer.getId());
        });

        // 4. Verify: avaliar se save nao deve ser chamado quando a regra falha
        Mockito.verify(customerRepository, Mockito.never()).save(Mockito.any(Customer.class));
    }
}
