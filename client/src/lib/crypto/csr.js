import { Pkcs10CertificateRequestGenerator } from '@peculiar/x509'

const signingAlgorithm = 'RSASSA-PKCS1-v1_5'
const hashAlgorithm = 'SHA-256'

export const RDN = {
  commonName: 'CN', 
  country: 'C',
  state: 'ST',
  locality: 'L',
  organization: 'O',
  organizationalUnit: 'OU',
  givenName: 'G',
  surname: 'SN',
  email: 'E'
}

/** @param {RDN} RDNs */
export const createCSR = async RDNs => {

  const keys = await crypto.subtle.generateKey(
    {
      name: signingAlgorithm,
      modulusLength: 1024,
      publicExponent: new Uint8Array([1, 0, 1]),
      hash: hashAlgorithm
    },
    true,
    ['sign', 'verify', 'encrypt', 'decrypt']
  )

  const csr = await Pkcs10CertificateRequestGenerator.create({
    name: Object.entries().map((rdn, value) => ({ [rdn]: [{ utf8String: value }] })),
    keys,
    signingAlgorithm
  })

  const csrBuffer = await Pkcs10CertificateRequestGenerator.export(csr)

  return {
    publicKey: await crypto.subtle.exportKey('spki', keys.publicKey),
    privateKey: await crypto.subtle.exportKey('pkcs8', keys.privateKey),
    csr: toPEM(csrBuffer, 'CERTIFICATE REQUEST')
  }
}

const toPEM = (buffer, type) => {
  const base64 = buffer.toString("base64").replace(/(.{64})/g, '$1\n')
  return `-----BEGIN ${type}-----\n${base64}\n-----END ${type}-----`
}