package benchmark.regex.benchmark.regex;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * FIXME: mdodsworth - Document me!
 *
 * @author mdodsworth
 */
public final class PhoneNumberTokenizer {
    private PhoneNumberTokenizer() {}

    public static Iterable<CharSequence> tokenizer(String phoneNumber) {
        List<CharSequence> tokens = Lists.newArrayList();

        // add the original token
        tokens.add(phoneNumber);


        return tokens;
    }
}
