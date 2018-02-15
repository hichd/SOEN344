/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package template;

import java.io.IOException;
import java.io.Reader;
import java.util.Vector;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.filters.ChainableReader;
import org.apache.tools.ant.types.Parameter;
import org.apache.tools.ant.types.RegularExpression;
import org.apache.tools.ant.util.regexp.Regexp;

/**
 * Filter which includes only those lines that contain the user-specified
 * regular expression matching strings.
 *
 * Example:
 * <pre>&lt;linecontainsregexp&gt;
 *   &lt;regexp pattern=&quot;foo*&quot;&gt;
 * &lt;/linecontainsregexp&gt;</pre>
 *
 * Or:
 *
 * <pre>&lt;filterreader classname=&quot;org.apache.tools.ant.filters.LineContainsRegExp&quot;&gt;
 *    &lt;param type=&quot;regexp&quot; value=&quot;foo*&quot;/&gt;
 * &lt;/filterreader&gt;</pre>
 *
 * This will fetch all those lines that contain the pattern <code>foo</code>
 *
 */
public final class LineContainsRegExp
    extends BaseParamFilterReader
    implements ChainableReader {
    /** Parameter name for the regular expression to filter on. */
    private static final String REGEXP_KEY = "regexp";

    /** Vector that holds the expressions that input lines must contain. */
    private Vector regexps = new Vector();

    /**
     * Constructor for "dummy" instances.
     *
     * @see BaseFilterReader#BaseFilterReader()
     */
    public LineContainsRegExp() {
        super();
    }

    /**
     * Creates a new filtered reader.
     *
     * @param in A Reader object providing the underlying stream.
     *           Must not be <code>null</code>.
     */
    public LineContainsRegExp(final Reader in) {
        super(in);
    }
    
    public int read() throws IOException {
    	return read(
    			(Parameter parameter) -> initialize(parameter) , 
    			() -> matches()
    			);
    }
    
    public boolean matches() {
		boolean matches = true;
		for (int i = 0; matches && i < regexps.size(); i++) {
		    RegularExpression regexp
		        = (RegularExpression) regexps.elementAt(i);
		    Regexp re = regexp.getRegexp(getProject());
		    matches = re.matches(line);
		}
		return matches;
	}

	public void initialize(Parameter parameter) {
		if (REGEXP_KEY.equals(parameter.getType())) {
		    String pattern = parameter.getValue();
		    RegularExpression regexp = new RegularExpression();
		    regexp.setPattern(pattern);
		    regexps.addElement(regexp);
		} else if (NEGATE_KEY.equals(parameter.getType())) {
		    setNegate(Project.toBoolean(parameter.getValue()));
		}
	}

    /**
     * Adds a <code>regexp</code> element.
     *
     * @param regExp The <code>regexp</code> element to add.
     *               Must not be <code>null</code>.
     */
    public void addConfiguredRegexp(final RegularExpression regExp) {
        this.regexps.addElement(regExp);
    }

    /**
     * Sets the vector of regular expressions which must be contained within
     * a line read from the original stream in order for it to match this
     * filter.
     *
     * @param regexps A vector of regular expressions which must be contained
     * within a line in order for it to match in this filter. Must not be
     * <code>null</code>.
     */
    private void setRegexps(final Vector regexps) {
        this.regexps = regexps;
    }

    /**
     * Returns the vector of regular expressions which must be contained within
     * a line read from the original stream in order for it to match this
     * filter.
     *
     * @return the vector of regular expressions which must be contained within
     * a line read from the original stream in order for it to match this
     * filter. The returned object is "live" - in other words, changes made to
     * the returned object are mirrored in the filter.
     */
    private Vector getRegexps() {
        return regexps;
    }

    /**
     * Creates a new LineContainsRegExp using the passed in
     * Reader for instantiation.
     *
     * @param rdr A Reader object providing the underlying stream.
     *            Must not be <code>null</code>.
     *
     * @return a new filter based on this configuration, but filtering
     *         the specified reader
     */
    public Reader chain(final Reader rdr) {
        LineContainsRegExp newFilter = new LineContainsRegExp(rdr);
        newFilter.setRegexps(getRegexps());
        newFilter.setNegate(isNegated());
        return newFilter;
    }
}
