package no.norwegian.currency.model;

import java.util.Objects;

/**
 * Represents the Currency Information available.
 */
public class CurrencyInformation {

    private final String content;

    public CurrencyInformation(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return content;
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final CurrencyInformation other = (CurrencyInformation) obj;
        return Objects.equals(this.content, other.content);
    }
}
