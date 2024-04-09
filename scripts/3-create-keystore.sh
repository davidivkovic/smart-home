#!/bin/bash

# Create a directory for keystores.
# mkdir output/keystores

# Merge Root CA and Intermediate CA keystores into one.
keytool -importkeystore -srckeystore ./output/root/ca.p12 -srcstoretype PKCS12 -destkeystore ./output/keystores/keystore.p12 -deststoretype PKCS12 -storepass:file ./config/ca-store-pass.txt -srcstorepass:file ./config/ca-store-pass.txt -noprompt
keytool -importkeystore -srckeystore ./output/intermediate/ca-intermediate-eu.p12 -srcstoretype PKCS12 -destkeystore ./output/keystores/keystore.p12 -deststoretype PKCS12 -storepass:file ./config/ca-store-pass.txt -srcstorepass:file ./config/ca-store-pass.txt -noprompt

mkdir -p ../smart-home-pki/pki/stores

# Move the keystore to the final application keystores directory.
mv ./output/keystores/keystore.p12 ../smart-home-pki/pki/stores
mv ./output/keystores/truststore.p12 ../smart-home-pki/pki/stores