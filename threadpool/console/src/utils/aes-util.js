import {Buffer} from 'buffer'
import crypto from 'crypto'

/**
 * generate key
 * @returns {string} key(length 16)
 */
export function genKey() {
    let chars = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    let result = '';
    for (let i = 16; i > 0; --i) result += chars[Math.floor(Math.random() * chars.length)];
    return result;
}

/**
 * encode message
 * @param msg message
 * @param key key
 * @returns {string} encoded message
 */
export function encrypt(msg, key) {
    try {
        let pwd = Buffer.from(key)
        let iv = crypto.randomBytes(12)
        let cipher = crypto.createCipheriv('aes-128-gcm', pwd, iv)
        let enc = cipher.update(msg, 'utf8', 'base64')
        enc += cipher.final('base64')
        let tags = cipher.getAuthTag()
        enc = Buffer.from(enc, 'base64')
        let totalLength = iv.length + enc.length + tags.length
        let bufferMsg = Buffer.concat([iv, enc, tags], totalLength)
        return bufferMsg.toString('base64')
    } catch (e) {
        console.log("Encrypt is error", e)
        return null
    }
}