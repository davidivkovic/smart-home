import { X509Certificate } from '@peculiar/x509'

export const certFromPEM = pem => {
  return new X509Certificate(pem)
}

/** @param {string} pem */
export const chainFromPEM = pem => {
  const chain = pem.match(/(?<=-----BEGIN CERTIFICATE-----)[\s\S]*?(?=-----END CERTIFICATE-----)/gm)
  return chain.map(c => new X509Certificate(`-----BEGIN CERTIFICATE-----${c}-----END CERTIFICATE-----`))
}