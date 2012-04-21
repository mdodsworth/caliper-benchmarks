package benchmark.regex;

import com.google.caliper.Param;
import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.regex.Pattern;

/**
 * Benchmarks for a variety of Regular Expression implementations
 *
 * @author mdodsworth
 */
public class RegexBenchmark extends SimpleBenchmark {

    @Param({"100", "1000"}) private int numberOfTokens;
    @Param private Expression expression;
    @Param private DataSource dataSource;
    @Param private RegexImpl regexImpl;

    private CharSequence[] tokens;

    private static enum Expression {
        EMAIL_ADDRESS("[a-z]*"),
        PHONE_NUMBER("[0-5]{3}");

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
                return RandomStringUtils.randomAlphabetic(10);
            }
        },
        RANDOM_ALPHANUMERIC {
            @Override
            public CharSequence nextToken() {
                return RandomStringUtils.randomAlphanumeric(10);
            }
        };

        public abstract CharSequence nextToken();
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

    public void timeRegexExpressions(int numReps) {
        for (int i = 0; i < numReps; i++) {
            for (CharSequence token : tokens) {
                regexImpl.matches(token);
            }
        }
    }

    public static void main(String[] args) {
        Runner.main(RegexBenchmark.class, args);
    }
}
