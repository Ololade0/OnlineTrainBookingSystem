package train.booking.train.booking.utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

@Component
public class PnrCodeGenerator {
    private final SecureRandom secureRandom = new SecureRandom();
    private final Set<Integer> generatedNumbers = new HashSet<>();

    private static final String PREFIX = "PNR";


    public String generateUniquePnrCodes() {

            int number;
            do {
                number = 100000 + secureRandom.nextInt(900000);
            } while (generatedNumbers.contains(number));

            generatedNumbers.add(number);
            return PREFIX + number;
        }
    }


