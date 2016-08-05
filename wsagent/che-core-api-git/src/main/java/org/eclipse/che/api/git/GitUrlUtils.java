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

import java.util.regex.Pattern;

/**
 * Utility class for working with Git urls.
 *
 * @author Vladyslav Zhukovskii
 * @author Kevin Pollet
 * @author Igor Vinokur
 */
public class GitUrlUtils {
    public static final Pattern GIT_SSH_URL_PATTERN =
            Pattern.compile("((((git|ssh)://)(([^\\\\/@:]+@)??)[^\\\\/@:]+)|([^\\\\/@:]+@[^\\\\/@:]+))(:|/)[^\\\\@:]+");
    public static final Pattern GIT_URL_WITH_CREDENTIALS_PATTERN = Pattern.compile("https?://[^:]+:.+@.*");

    private GitUrlUtils() {

    }

    /**
     * Check that given url is ssh url.
     *
     * @param url
     *         git url
     * @return {@code true} if given url is ssh url or {@code false} if not
     */
    public static boolean isSSH(String url) {
        return url != null && GIT_SSH_URL_PATTERN.matcher(url).matches();
    }

    /**
     * Check that given url contains credentials.
     *
     * @param url
     *         git url
     * @return {@code true} if given url contains credentials or {@code false} if not
     */
    public static boolean containsCredentials(String url) {
        return url != null && GIT_URL_WITH_CREDENTIALS_PATTERN.matcher(url).matches();
    }

    /**
     * Returns username if given url contains credentials, otherwise returns {@code null}.
     *
     * @param url
     *         http or https git url with credentials
     * @return git provider username or {@code null} if given url does not contain credentials
     */
    public static String getUsername(String url) {
        if (url == null || !containsCredentials(url)) {
            return null;
        }
        return url.substring(url.indexOf("://") + 3, url.lastIndexOf(":"));
    }

    /**
     * Returns password if given url contains credentials, otherwise returns {@code null}.
     *
     * @param url
     *         http or https git url with credentials
     * @return git provider password or {@code null} if given url does not contain credentials
     */
    public static String getPassword(String url) {
        if (url == null || !containsCredentials(url)) {
            return null;
        }
        return url.substring(url.lastIndexOf(":") + 1, url.indexOf("@"));
    }
}
