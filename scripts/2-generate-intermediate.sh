#!/bin/bash

# Generate Intermediate CA keypair and certificate.
echo "Generating Intermediate CA keypair."
openssl genrsa -out intermediate-ca-eu.key -aes256 -passout file:./config/ca-inter-cert-eu-pass.txt 2048

echo "Generating Intermediate CA certificate CSR."
openssl req -new -key intermediate-ca-eu.key -out intermediate-ca-eu.csr -sha256 -passin file:./config/ca-inter-cert-eu-pass.txt -config ./config/intermediate-ca-eu.cnf

echo "Signing Intermediate CA certificate by Root CA."
openssl x509 -req -in intermediate-ca-eu.csr -CA ./output/root/root-ca.pem -CAkey ./output/root/root-ca.key -CAcreateserial -out intermediate-ca-eu.pem -days 1825 -passin file:./config/ca-root-cert-pass.txt -extfile ./config/intermediate-ca-eu.cnf -extensions intermediate_ca_extensions
cat ./output/root/root-ca.pem >> intermediate-ca-eu.pem

echo "Intermediate CA certificate created successfully."
openssl x509 -in intermediate-ca-eu.pem -out intermediate-ca-eu.crt -outform der
sleep 1

# Export intermediate CA's key to certificate authority keystore.
echo "Exporting Intermediate CA keypair and certificate into a PKCS12 keystore."
openssl pkcs12 -export -out ca-intermediate-eu.p12 -name "ca-intermediate-eu" -inkey intermediate-ca-eu.key -in intermediate-ca-eu.pem -passout file:./config/ca-store-pass.txt -passin file:./config/ca-inter-cert-eu-pass.txt
echo "Exported successfully."
sleep 1

# Remove unused Intermediate CA files
echo "Removing unnecessary files."
rm ./intermediate-ca-eu.csr

# Move Root CA files
echo "Moving files to separate folders."
mkdir output/intermediate
mv ./intermediate-ca-eu.key ./output/intermediate
mv ./intermediate-ca-eu.crt ./output/intermediate
mv ./intermediate-ca-eu.pem ./output/intermediate
mv ./ca-intermediate-eu.p12 ./output/intermediate
# cp ./output/root/truststore.p12 ./output/intermediate

read -p "Press enter to continue"