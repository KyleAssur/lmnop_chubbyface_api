package za.ac.cput.domain;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Objects;

@Entity
public class Support {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  long messageId;
    private String firstName;
    private String email;;
    private String message;

    protected Support() {}

    private Support(Builder builder) {
        this.messageId = builder.messageId;
        this.firstName = builder.firstName;
        this.email = builder.email;
        this.message = builder.message;
    }

    public  long getMessageId() {
        return messageId;
    }

    public String getFirstName() {
        return firstName;
    }


    public String getEmail() {
        return email;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Support that = (Support) o;
        return messageId == that.messageId && Objects.equals(firstName, that.firstName) && Objects.equals(email, that.email) && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, firstName, email, message);
    }

    @Override
    public String toString() {
        return "ContactSupport{" +
                "messageId=" + messageId +
                ", firstName='" + firstName + '\'' +
                ", email='" + email + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    public static class Builder {
        private long messageId;
        private String firstName;
        private String email;
        private String message;

        public Builder setMessageId(long messageId) {
            this.messageId = messageId;
            return this;
        }

        public Builder setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }


        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder copy(Support contactMessage) {
            this.messageId = contactMessage.messageId;
            this.firstName = contactMessage.firstName;
            this.email = contactMessage.email;
            this.message = contactMessage.message;
            return this;
        }
        public Support build() {
            return new Support(this);
        }
    }


}
