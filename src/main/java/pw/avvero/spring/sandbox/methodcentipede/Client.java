package pw.avvero.spring.sandbox.methodcentipede;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table
@Entity

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    @Id
    @SequenceGenerator(name = "client_id_seq", sequenceName = "client_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_id_seq")
    @Column(name = "id", columnDefinition = "serial")
    private Integer id;
    private String email;
    private String firstName;
    private String lastName;
    @Version
    private long version;
}
