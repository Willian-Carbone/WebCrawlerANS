import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import Metodos.Utilitarios
import org.jsoup.Jsoup
import java.nio.file.*
import java.nio.charset.StandardCharsets
import java.util.zip.ZipFile

import static org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.io.TempDir

class UtilitariosSpec {

    @TempDir
    Path tempDir


    @Test
    @DisplayName("Teste validador email")
    void testeValidadorEmail() {

        [
                [email: "teste@ans.br", valido: true],
                [email: "erro-sem-arroba", valido: false],
                [email: "admin@gov.br", valido: true],
                [email:"123",valido:false]
        ].each { cenario ->

            boolean resultado = Utilitarios.validadorEmail(cenario.email as String)
            assertEquals(cenario.valido, resultado,)
        }
    }

    @Test
    @DisplayName ("Teste validador baixarArquivo")
    void testeBaixarArquivo(){

        String urlTeste = "https://www.google.com/robots.txt"
        String nomeArquivo = "robots.txt"


        String localDestino = tempDir.toString()


        Utilitarios.baixarArquivo(urlTeste, nomeArquivo, localDestino)


        Path arquivoEsperado = tempDir.resolve(nomeArquivo)

        assertTrue(Files.exists(arquivoEsperado), "O arquivo deveria ter sido baixado")
        assertTrue(Files.size(arquivoEsperado) > 0,)



    }

    @Test
    @DisplayName(" converter elementos HTML em um arquivo CSV ")
    void testCriadorCsvSucesso() {
        String htmlSimulado = """
            <table>
                <tr>
                    <th>Competência</th>
                    <th>Publicação</th>
                </tr>
                <tr>
                    <td>março/2026</td>
                    <td>01/03/2026</td>
                </tr>
            </table>
        """
        Document doc = Jsoup.parse(htmlSimulado)
        Elements linhas = doc.select("tr")

        String nomeArquivo = "tabela_teste"
        String localDestino = tempDir.toString()


        Utilitarios.criadorCsv(linhas, nomeArquivo, localDestino)

        Path arquivoCsv = tempDir.resolve(nomeArquivo + ".csv")

        assertTrue(Files.exists(arquivoCsv), "O arquivo CSV deveria ter sido criado")


        List<String> conteudo = Files.readAllLines(arquivoCsv, StandardCharsets.UTF_8)

        assertEquals(2, conteudo.size(), "O CSV deveria ter 2 linhas")


        assertEquals('"Competência";"Publicação"', conteudo[0])
        assertEquals('"março/2026";"01/03/2026"', conteudo[1])
    }

    @Test
    @DisplayName("Teste zip pasta")
    void testeziper(){

            Path pastaOrigem = tempDir.resolve("arquivos_para_zipar")
            Files.createDirectories(pastaOrigem)


            Path arquivoValido = pastaOrigem.resolve("relatorio.csv")
            Files.write(arquivoValido, "dados da ans".getBytes())


            Path pastaProibida = pastaOrigem.resolve("rep.ConceitoSaude")
            Files.createDirectories(pastaProibida)

            Path arquivoInvalido = pastaProibida.resolve("nao_incluir.txt")
            Files.write(arquivoInvalido, "deve ser ignorado".getBytes())


            File zipGerado = Utilitarios.ziparPasta(pastaOrigem,tempDir)


            assertTrue(zipGerado.exists(), "O arquivo ZIP deveria ter sido criado")


            ZipFile zipFile = new ZipFile(zipGerado)
            List<String> entradas = zipFile.entries().collect { it.name }


            assertTrue(entradas.contains("relatorio.csv"), "O ZIP deveria conter o relatorio.csv")

            assertFalse(entradas.any { it.contains("rep.ConceitoSaude") }, "O ZIP nao deveria conter a pasta rep.ConceitoSaude")

            zipFile.close()



    }

    @Test
    @DisplayName("Deve retornar falso se as credenciais de e-mail não estiverem configuradas")
    void testFalhaSemConfiguracao() {

        File mockFile = new File("teste.zip")

        boolean resultado = Utilitarios.enviarParaInteressado("teste@destino.com", mockFile)

        assertFalse(resultado, "O envio não deveria ocorrer sem credenciais")
    }



}
