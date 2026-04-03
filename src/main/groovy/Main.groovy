import Metodos.Crawler
import Metodos.JsonControler
import Metodos.Utilitarios

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

static void main(String[] args) {
    Crawler c = new Crawler()
    Scanner scan = new Scanner(System.in)

    println("Pressione 1 para baixar os componentes , 2 para baixar o historico de versões pós janeiro de 2016, 3 para baixar a tabela de erros de envio, 4 para editar emails de interessados, 5 para disparar relatorios a emails interessados, 6 para sair")
    String entrada = scan.nextLine()

    while (entrada != "6") {
        while (!Utilitarios.validarEntrada(["1", "2", "3", "4", "5"], entrada)) {
            println("Insira uma entrada valida")
            entrada = scan.nextLine()
        }
        if (entrada == "1") {

            ArrayList<String> componentes = ["Organizacional", "Conteudo e estrutura", "Representação de conceito em saude", "Segurança e privacidade", "Comunicação"]

            Map<String, String> arquivosaParaBaixar = [:]

            componentes.forEach { comp ->
                println("Deseja baixar o componente ${comp}? S/N")
                String resp = scan.nextLine().toUpperCase()

                while (!Utilitarios.validarEntrada(["S", "N"], resp)) {
                    println("Digite um valor válido")
                    resp = scan.nextLine().toUpperCase()

                }

                if (resp == "S") {
                    println("Informe o nome que deseja que o arquivo se chame")
                    String nomeArq = scan.nextLine()
                    arquivosaParaBaixar.put(comp, nomeArq)
                }


            }

            println("Fazendo download.....")

            c.baixarComponentes(arquivosaParaBaixar)

            println("Download dos arquivos feitos")
        }

        if (entrada == "2" || entrada == "3") {
            println("Insira o nome de salvamento do arquivo")
            String nome = scan.nextLine()
            println("Baixando arquivo selecionado ...")

            if (entrada == "2") {
                c.baixarHistoricoVersoes(nome)
            } else {
                c.baixarErrosEnvio(nome)
            }
            println("Download concluido")
        }

        if (entrada == "4") {
            TerminalEmail.terminal(scan)
        }

        if (entrada == "5") {
            Path raizDoProjeto = Paths.get("").toAbsolutePath()
            Path pastaDownload = raizDoProjeto.resolve("../../../").resolve("downloads")

            if (!Files.exists(pastaDownload)) {
                println("Nenhum dowload foi feito, faça algum antes de mandar para os emails interessados")
            } else {
                Map emails = JsonControler.capturarEmails()
                if(!emails){
                    println("Nenhum email registrado,registre pelo menos um email primeiro")
                }
                File pastaZipada = Utilitarios.ziparPasta(pastaDownload)

                emails.values().each { email ->

                    if(email instanceof Integer){return}

                    println("-" * 20)

                    println("Enviando relatorio para ${email}")
                    boolean resultado = Utilitarios.enviarParaInteressado(email as String, pastaZipada)

                    if (resultado) {
                        println("Sucesso no envio")
                    } else {
                        println("Falha no envio")
                    }

                }


            }
        }


        println("Pressione 1 para baixar os componentes , 2 para baixar o historico de versões pós janeiro de 2016, 3 para baixar a tabela de erros de envio, 4 para editar emails de interessados, 5 para disparar relatorios a emails interessados, 6 para sair")
        entrada = scan.nextLine()
    }

}