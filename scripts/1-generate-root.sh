#!/bin/bash

# Generate Root CA keypair and certificate.
echo "Generating Root CA keypair."
openssl genrsa -aes256 -out root-ca.key -passout file:./config/ca-root-cert-pass.txt 2048
echo "Generating Root CA self-signed certificate."
openssl req -x509 -new -key root-ca.key -out root-ca.pem -sha256 -days 3650 -passin file:./config/ca-root-cert-pass.txt -config ./config/root-ca.cnf -extensions root_ca_extensions
echo "Root CA certificate created successfully."
openssl x509 -in root-ca.pem -inform pem -out root-ca.crt -outform der
openssl x509 -in root-ca.pem -inform pem -out root-ca.der -outform der
sleep 1

# Export CA's key and certificate to a keystore.
echo "Exporting Root CA keypair and certificate into a PKCS12 keystore."
openssl pkcs12 -export -in root-ca.pem -inkey root-ca.key -out ca.p12 -name "ca-root" -passin file:./config/ca-root-cert-pass.txt -passout file:./config/ca-store-pass.txt
echo "Exported successfully."
sleep 1

# Export CA's certificate to a truststore.
echo "Exporting Root CA certificate into a truststore."
keytool -import -storetype PKCS12 -noprompt -trustcacerts -alias "ca-root" -file root-ca.pem -keystore truststore.p12 -storepass:file ./config/ca-store-pass.txt
echo  "Exported successfully."
sleep 1

# Move Root CA files
echo "Moving files to separate folders."
rm -rf ./output
mkdir output
mkdir output/root
mkdir output/keystores
mv ./root-ca.key ./output/root
mv ./root-ca.crt ./output/root
mv ./root-ca.pem ./output/root
mv ./root-ca.der ./output/root
mv ./ca.p12 ./output/root
mv ./truststore.p12 ./output/keystores
# mv ./ca.p12 ../smart-home-pki/pki/stores

read -p "Press enter to continue"