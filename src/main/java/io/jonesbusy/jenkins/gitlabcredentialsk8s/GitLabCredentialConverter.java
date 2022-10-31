/*
 * The MIT License
 *
 * Copyright 2022 Valentin Delaye
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.jonesbusy.jenkins.gitlabcredentialsk8s;

import com.cloudbees.jenkins.plugins.kubernetes_credentials_provider.CredentialsConvertionException;
import com.cloudbees.jenkins.plugins.kubernetes_credentials_provider.SecretToCredentialConverter;
import com.cloudbees.jenkins.plugins.kubernetes_credentials_provider.SecretUtils;
import com.cloudbees.plugins.credentials.CredentialsScope;
import edu.umd.cs.findbugs.annotations.CheckForNull;
import io.jenkins.plugins.gitlabserverconfig.credentials.PersonalAccessTokenImpl;
import hudson.Extension;
import io.fabric8.kubernetes.api.model.Secret;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;

/**
 * SecretToCredentialConverter that converts {@link PersonalAccessTokenImpl }.
 */
@Extension
public class GitLabCredentialConverter extends SecretToCredentialConverter {
    private static final Logger LOG = Logger.getLogger(GitLabCredentialConverter.class.getName());

    @Override
    public boolean canConvert(String type) {
        return "gitlabToken".equals(type);
    }

    @Override
    public PersonalAccessTokenImpl convert(Secret secret) throws CredentialsConvertionException {
        String bearerTokenBase64 = SecretUtils.getNonNullSecretData(secret, "text", "gitlabToken credential is missing the token (in the text property)");
        String bearerToken = SecretUtils.requireNonNull(base64DecodeToString(bearerTokenBase64), "gitlabToken credential has an invalid token (the data in the text property must be base64 encoded UTF-8)");
        
        return new PersonalAccessTokenImpl(
                CredentialsScope.GLOBAL,
                SecretUtils.getCredentialId(secret),
                SecretUtils.getCredentialDescription(secret),
                bearerToken);
    }

    /**
     * Convert a String representation of the base64 encoded bytes back to a byte[].
     * @param s the base64 encoded representation of the bytes.
     * @return the byte[] or {@code null} if the string could not be converted.
     */
    @CheckForNull // TODO: use the one from SecretUtils when allowed
    private static byte[] base64Decode(String s) {
        try {
            return Base64.getDecoder().decode(s);
        } catch (IllegalArgumentException ex) {
            LOG.log(Level.WARNING, "failed to base64decode Secret, is the format valid?  {0}", ex.getMessage());
        }
        return null;
    }

    /**
     * Convert a String representation of the base64 encoded bytes of a UTF-8 String back to a String.
     * @param s the base64 encoded String representation of the bytes.
     * @return the String or {@code null} if the string could not be converted.
     */
    @CheckForNull // TODO: use the one from SecretUtils when allowed
    public static String base64DecodeToString(String s) {
        byte[] bytes = base64Decode(s);
        if (bytes != null) {
            try {
                CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
                decoder.onMalformedInput(CodingErrorAction.REPORT);
                decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
                CharBuffer decode = decoder.decode(ByteBuffer.wrap(bytes));
                return decode.toString();
            } catch (CharacterCodingException ex) {
                LOG.log(Level.WARNING, "failed to covert Secret, is this a valid UTF-8 string?  {0}", ex.getMessage());
            }
        }
        return null;
    }
}
