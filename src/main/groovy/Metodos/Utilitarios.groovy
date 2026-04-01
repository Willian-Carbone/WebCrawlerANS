package Metodos

import groovyx.net.http.FromServer
import groovyx.net.http.HttpBuilder
import org.jsoup.select.Elements
import static groovyx.net.http.HttpBuilder.configure
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

class Utilitarios {

    static boolean validadorEmail(String email) {
        return (email ==~ /[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}/)
    }


    static void baixarArquivo(String url, String nomeArquivo, String local) {
        Path raizDoProjeto = Paths.get("").toAbsolutePath()
        Path pastaDestino = raizDoProjeto.resolve("../../../").resolve(local)

        Path arquivoCompleto = pastaDestino.resolve(nomeArquivo)


        if (!Files.exists(pastaDestino)) {
            Files.createDirectories(pastaDestino)
        }


        HttpBuilder htpp = configure {
            request.uri = url
        }


        htpp.get {

            response.parser('*/*') { config, resp ->
                return resp.inputStream
            }

            response.success { FromServer fs ->

                Files.copy(fs.inputStream, arquivoCompleto, StandardCopyOption.REPLACE_EXISTING)

            }


        }


    }


    static void criadorCsv(Elements linhasDaTabela, String nomeArquivo, String local) {
        Path raizDoProjeto = Paths.get("").toAbsolutePath()
        Path pastaDestino = raizDoProjeto.resolve("../../../").resolve(local)

        Path arquivoCompleto = pastaDestino.resolve(nomeArquivo + ".csv")

        File arquivoCsv = arquivoCompleto.toFile()

        if (!Files.exists(pastaDestino)) {
            Files.createDirectories(pastaDestino)
        }

        arquivoCsv.withWriter("UTF-8") { writer ->
            linhasDaTabela.each { tr ->

                Elements celulas = tr.select("th, td")


                String campo = celulas.collect { celula -> celula.text() }.join(";")

                writer.writeLine(campo)
            }
        }


    }


}
