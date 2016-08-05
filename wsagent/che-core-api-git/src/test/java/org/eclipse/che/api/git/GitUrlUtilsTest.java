/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.api.git;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Tests for {@link GitUrlUtils}.
 *
 * @author Igor Vinokur
 */
public class GitUrlUtilsTest {

    @DataProvider(name = "validGitSshUrlsProvider")
    public static Object[][] validGitSshUrls() {
        return new Object[][]{{"ssh://user@host.xz:port/path/to/repo.git"},
                              {"ssh://user@host.xz/path/to/repo.git"},
                              {"ssh://host.xz:port/path/to/repo.git"},
                              {"ssh://host.xz/path/to/repo.git"},
                              {"ssh://user@host.xz/path/to/repo.git"},
                              {"ssh://host.xz/path/to/repo.git"},
                              {"ssh://user@host.xz/~user/path/to/repo.git"},
                              {"ssh://host.xz/~user/path/to/repo.git"},
                              {"ssh://user@host.xz/~/path/to/repo.git"},
                              {"ssh://host.xz/~/path/to/repo.git"},
                              {"user@host.xz:/path/to/repo.git"},
                              {"user@host.xz:path/to/repo.git"},
                              {"git://host.xz/path/to/repo.git"},
                              {"git://host.xz/~user/path/to/repo.git"},
                              {"git@vcsProvider.com:user/test.git"},
                              {"ssh@vcsProvider.com:user/test.git"}};
    }

    @DataProvider(name = "http(s)GitUrlsProvider")
    public static Object[][] httpAndHttpsGitUrls() {
        return new Object[][]{{"http://host.xz/path/to/repo.git"},
                              {"https://host.xz/path/to/repo.git"}};
    }

    @DataProvider(name = "gitUrlsWithCredentialsProvider")
    public static Object[][] gitUrlsWithCredentials() {
        return new Object[][]{{"http://username:password@host.xz/path/to/repo.git"},
                              {"https://username:password@host.xz/path/to/repo.git"}};
    }

    @Test(dataProvider = "validGitSshUrlsProvider")
    public void shouldReturnTrueIfGivenUrlIsSsh(String url) throws Exception {
        assertTrue(GitUrlUtils.isSSH(url));
    }

    @Test(dataProvider = "http(s)GitUrlsProvider")
    public void shouldReturnFalseIfGivenUrlIsNotSsh(String url) throws Exception {
        assertFalse(GitUrlUtils.isSSH(url));
    }

    @Test(dataProvider = "gitUrlsWithCredentialsProvider")
    public void shouldReturnTrueIfGivenUrlContainsCredentials(String url) throws Exception {
        assertTrue(GitUrlUtils.containsCredentials(url));
    }

    @Test(dataProvider = "http(s)GitUrlsProvider")
    public void shouldReturnFalseIfGivenUrlDoesNotContainUserNameAndPassword(String url) throws Exception {
        assertFalse(GitUrlUtils.containsCredentials(url));
    }

    @Test(dataProvider = "gitUrlsWithCredentialsProvider")
    public void shouldReturnUsernameIfGivenUrlContainsCredentials(String url) throws Exception {
        assertEquals(GitUrlUtils.getUsername(url), "username");
    }

    @Test(dataProvider = "gitUrlsWithCredentialsProvider")
    public void shouldReturnPasswordIfGivenUrlContainsCredentials(String url) throws Exception {
        assertEquals(GitUrlUtils.getPassword(url), "password");
    }
}
