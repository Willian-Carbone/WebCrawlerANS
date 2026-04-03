import Metodos.JsonControler
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

import java.nio.file.*


import static org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.io.TempDir


class JsonControlerSpec {

    @TempDir Path tempDir

    @Test
    @DisplayName("Teste salvamento email em json")
    void salvarEmail(){

        Map<String, String> dadosTeste = [
                "1": "email@exemplo.com",
                "2": "contato@exemplo.gov.br"
        ]


        String nomeEsperado = "emails.json"


        JsonControler.salvarEmails(dadosTeste, tempDir)


        Path arquivoGerado = tempDir.resolve(nomeEsperado)

        assertTrue(Files.exists(arquivoGerado), "O arquivo emails.json deveria existir")


        Object jsonLido = new JsonSlurper().parse(arquivoGerado.toFile())

        assertEquals("email@exemplo.com", jsonLido["1"])
        assertEquals("contato@exemplo.gov.br", jsonLido["2"])


    }


    @Test
    @DisplayName("Deve carregar o Map de e-mails com sucesso a partir de um JSON")
    void testCapturarEmailsSucesso() {

        Path arquivoPath = tempDir.resolve("emails.json")
        Map dadosOriginais = ["1":"exemplo@gamil.com" , "2": "contato@email.gov.br"]


        arquivoPath.toFile().text = new JsonBuilder(dadosOriginais).toPrettyString()


        Map resultado = JsonControler.capturarEmails(tempDir)

        assertNotNull(resultado)
        assertEquals(2, resultado.size())
        assertEquals( "contato@email.gov.br", resultado["2"])
        assertEquals("exemplo@gamil.com" , resultado["1"])
    }
}
