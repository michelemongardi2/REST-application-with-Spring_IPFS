package com.example.ipfsdemo;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/* - MultipartFile: rappresentazione di un file caricato ricevuto in una multipart request.
 *  - InputStream: flusso ordinato di byte.
 *    Permette di leggere i dati da un Java InputStream come una sequenza ordinata di byte.
 *    Ciò è utile durante la lettura di dati da un file o per la sua ricezione in rete.
 *  - NamedStreamable: utilizzato per passare a IPFS una sequenza di byte (NamedStreamble.InputStreamWrapper)
 *  - MerkleNode: quando si aggiunge un file o una directory a IPFS,
 *    questa operazione restituirà un nuovo ramo dedicato dell'albero Merkle composto da uno o più oggetti collegati.
 *    Un MerkleNode è costituito dalle seguenti informazioni: Hash (multihash): l'identificatore univoco dell'oggetto in IPFS.
 *    link (zero, uno o più): elenco degli oggetti figlio.
 *    Per convertire il Multihash ottenuto dall'aggiunta del file (dentro al merkle node) in Base58:
 *    String hash = multihash.toBase58();
 *    merkleNode.hash.toBase58() --> siccome il merklenode contiene tante informazioni, recuperiamo dall'oggetto ottenuto il multihash e lo convertiamo in base58.
 *  - Multihash filePointer = Multihash.fromBase58(hash) -> multihash è un hash autodescrittivo utilizzato
 *    per identificare in modo univoco gli oggetti e individuarli nell'albero IPFS Merkle.
 *    Di solito è rappresentato da Base58, ma possiamo anche usare il formato esadecimale.
 *    I multihash sono costituiti da parti diverse: ad esempio legge l'hash Base58 in Multihash -> Multihash multihash = Multihash.fromBase58("QmT78zSuBmuS4z925WZfrqQ1qHaJ56DQaTfyMUF7F8ff5o");
 *    In sostanza, vogliamo ottenere il formato multihash dell'hash del file che si vuole recuperare (che è in base58)
 */

@Service
public class IPFSService implements FileServiceImpl{

    @Autowired
    private IPFSConfig ipfsConfig;

    @Override
    public String saveFile(MultipartFile file) {

        try {
            InputStream stream = new ByteArrayInputStream(file.getBytes());
            NamedStreamable.InputStreamWrapper inputStreamWrapper = new NamedStreamable.InputStreamWrapper(stream);
            IPFS ipfs = ipfsConfig.ipfs;

            MerkleNode merkleNode = ipfs.add(inputStreamWrapper).get(0);

            return merkleNode.hash.toBase58();

        }catch(Exception e){
            throw new RuntimeException("Error during the communication with the IPFS node: ", e);
        }
    }

    @Override
    public byte[] loadFile(String hash) {

        try{
            IPFS ipfs = ipfsConfig.ipfs;

            Multihash filePointer = Multihash.fromBase58(hash);
            return ipfs.cat(filePointer);
        } catch (Exception e) {
            throw new RuntimeException("Error during the communication with the IPFS node: ", e);
        }
    }
}


