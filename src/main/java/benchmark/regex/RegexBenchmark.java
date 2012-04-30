package benchmark.regex;

import com.google.caliper.Param;
import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;

import java.util.regex.Pattern;

import static org.apache.commons.lang3.RandomStringUtils.*;

/**
 * Benchmarks for a variety of Regular Expression implementations
 *
 * @author mdodsworth
 */
public class RegexBenchmark extends SimpleBenchmark {

    @Param({"1000"}) private int numberOfTokens;
    @Param private Expression expression;
    @Param private DataSource dataSource;
    @Param private RegexImpl regexImpl;

    private CharSequence[] tokens;

    private static enum Expression {
        PHONE_NUMBER("^((\\+\\d{1,3}(-|\\.)?\\(?\\d\\)?(-|\\.)?\\d{1,5})|(\\(?\\d{2,6}\\)?))(-|\\.)?(\\d{3,4})" +
                "(-|\\.)?(\\d{4})((x|ext)\\d{1,5}){0,1}$"),
        NEW_PHONE_NUMBER("^(?>\\+?\\(?\\d{1,3}+\\)?[-.]?)?(?:\\(?\\d{3,6}+\\)?[-.]?)?\\d{3,4}[-.]?\\d{4}(?>(x|ext)\\d{1,5}+)?$"),
        /*best..but not above number*/LOOK_AHEAD("^(?=\\+?[\\d-.\\(\\)]{7,24}+(?>(x|ext)\\d{1,5}+)?$)(?>(?>\\+?\\d{1,3}?|\\(\\d{1,3}+\\))[-.]?)?(?:\\(\\d{3,6}+\\)|\\d{3,6})?[-.]?\\d{3,4}[-.]?\\d{4}(?>(x|ext)\\d{1,5}+)?$"),
        NEW_LOOK_AHEAD("^(?=\\+?[\\d-.\\(\\)]{7,24}+(?>(x|ext)\\d{1,5}+)?$)(?>\\+?\\(?\\d{1,3}+\\)?[-.]?)?(?:\\(?\\d{3,6}+\\)?[-.]?)?\\d{3,4}[-.]?\\d{4}(?>(x|ext)\\d{1,5}+)?$");

        private final String expression;

        private Expression(String expression) {
            this.expression = expression;
        }

        public String getExpression() {
            return expression;
        }
    }

    private static enum DataSource {
        RANDOM_ALPHABETIC {
            @Override
            public CharSequence nextToken() {
                return randomAlphabetic(20);
            }
        },
        RANDOM_ALPHANUMERIC {
            @Override
            public CharSequence nextToken() {
                return randomAlphanumeric(20);
            }
        },
        PHONE_NUMBER {
            @Override
            public CharSequence nextToken() {
                return "1-" + randomNumeric(3) + '-' + randomNumeric(3) + "-" + randomNumeric(4) + "x" + randomNumeric(5);
            }
        },
        NUMBER {
            @Override
            public CharSequence nextToken() {
                return randomNumeric(20);
            }
        };

        abstract CharSequence nextToken();
    }

    private static enum RegexImpl {
        JDK_REGEX {
            private Pattern pattern;

            @Override
            public void initialize(Expression expression) {
                pattern = Pattern.compile(expression.getExpression());
            }

            @Override
            public boolean matches(CharSequence charSequence) {
                return pattern.matcher(charSequence).matches();
            }
        };

        public abstract void initialize(Expression expression);
        public abstract boolean matches(CharSequence charSequence);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        tokens = new CharSequence[numberOfTokens];
        for (int i = 0; i < numberOfTokens; i++) {
            tokens[i] = dataSource.nextToken();
        }

        regexImpl.initialize(expression);
    }

    //======== benchmarks ========//

    public void timeRegexExpressions(int numReps) {
        for (int i = 0; i < numReps; i++) {
            for (CharSequence token : tokens) {
                regexImpl.matches(token);
            }
        }
    }

    //======== main ========//

    public static void main(String[] args) {
        Runner.main(RegexBenchmark.class, args);
    }
}
