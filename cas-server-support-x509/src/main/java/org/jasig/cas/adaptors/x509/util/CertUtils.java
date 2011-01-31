/*
 * Copyright 2007 The JA-SIG Collaborative. All rights reserved. See license
 * distributed with this file and available online at
 * http://www.uportal.org/license.html
 */
package org.jasig.cas.adaptors.x509.util;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Date;

import org.springframework.core.io.Resource;


/**
 * Utility class with methods to support various operations on X.509 certs.
 *
 * @author Marvin S. Addison
 * @version $Revision$
 * @since 3.4.7
 *
 */
public final class CertUtils {
    /** X509 certificate type. */
    public static final String X509_CERTIFICATE_TYPE = "X509";
    
    /** Suppressed constructor of utility class. */
    private CertUtils() { /*No initialization required*/ }
    

    /**
     * Determines whether the given CRL is expired by examining the nextUpdate field.
     *
     * @param crl CRL to examine.
     *
     * @return True if current system time is after CRL next update, false otherwise.
     */
    public static boolean isExpired(final X509CRL crl) {
        return isExpired(crl, new Date(System.currentTimeMillis()));
    }

    /**
     * Determines whether the given CRL is expired by comparing the nextUpdate field
     * with a given date.
     *
     * @param crl CRL to examine.
     * @param reference Reference date for comparison.
     *
     * @return True if reference date is after CRL next update, false otherwise.
     */   
    public static boolean isExpired(final X509CRL crl, final Date reference) {
        return reference.after(crl.getNextUpdate());
    }

    /**
     * Fetches an X.509 CRL from a resource such as a file or URL.
     *
     * @param resource Resource descriptor.
     *
     * @return X.509 CRL
     * 
     * @throws IOException On IOErrors.
     * @throws CRLException On CRL parse errors.
     */
    public static X509CRL fetchCRL(final Resource resource) throws CRLException, IOException {
        // Always attempt to open a new stream on the URL underlying the resource
        final InputStream in = resource.getURL().openStream();
        try {
            return (X509CRL) CertUtils.getCertificateFactory().generateCRL(in);
        } finally {
            in.close();
        }
    }

    /**
     * Creates a unique and human-readable representation of the given certificate.
     *
     * @param cert Certificate.
     *
     * @return String representation of a certificate that includes the subject and serial number.
     */
    public static String toString(final X509Certificate cert) {
        return String.format("%s, SerialNumber=%s", cert.getSubjectDN(), cert.getSerialNumber());
    }

    /**
     * Gets a certificate factory for creating X.509 artifacts.
     * 
     * @return X509 certificate factory.
     */
    public static CertificateFactory getCertificateFactory() {
        try {
            return CertificateFactory.getInstance(X509_CERTIFICATE_TYPE);
        } catch (CertificateException e) {
            throw new IllegalStateException("X509 certificate type not supported by default provider.");
        }
    }
}
