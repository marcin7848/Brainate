FROM jordi/openssl
WORKDIR /certificate
CMD openssl pkcs12 -export -in certificate.crt -inkey private.key -out keystore.p12 -name "brainate" -passin pass:pass123 -passout pass:pass123