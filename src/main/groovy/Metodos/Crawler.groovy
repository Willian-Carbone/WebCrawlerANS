package Metodos


import groovyx.net.http.HttpBuilder
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import static groovyx.net.http.HttpBuilder.configure

class Crawler {
    Document paginaHome
    HttpBuilder http

    Crawler() {

        this.http = configure {
            request.uri = 'https://www.gov.br/ans/pt-br'
            request.headers['User-Agent'] = 'Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:148.0) Gecko/20100101 Firefox/148.0'
            request.headers['Accept'] = '*/*'

        }


        String htmltBrutoANS = this.http.get()
        Document paginaAns = Jsoup.parse(htmltBrutoANS)

        String linkAssuntosPrestadores = paginaAns.select("div.row.tile-default > div.row-content > div:nth-child(4) a").first().attr('abs:href')

        String htmlBrutoEspacoPrestador = this.http.get {
            request.uri = linkAssuntosPrestadores
        }

        Document paginaEspacoPrestador = Jsoup.parse(htmlBrutoEspacoPrestador)
        String linkPaginaHome = paginaEspacoPrestador.select("a.govbr-card-content").first().attr("abs:href")

        String htmlBrutoPaginaHome = this.http.get {
            request.uri = linkPaginaHome
        }

        this.paginaHome = Jsoup.parse(htmlBrutoPaginaHome)
    }




    void baixarComponentes() {

        String linkPadraoTissAtualizado = paginaHome.select("p.callout > a").first().attr("abs:href")

        String htmlBrutoPadraoTiss = this.http.get {
            request.uri = linkPadraoTissAtualizado
        }

        Document paginaTissAtualizado = Jsoup.parse(htmlBrutoPadraoTiss)


        ArrayList<String> listaLinks = []

        for (int i = 0; i <= 4; i++) {
            listaLinks.add(paginaTissAtualizado.select("table tr:eq($i) a").first().attr("abs:href"))

        }

        Utilitarios.baixarArquivo(listaLinks[0], "Componente_Organizacional_atualizado.pdf", "downloads/componentes/organizacional")
        Utilitarios.baixarArquivo(listaLinks[1], "Componente_Conteudo_Estrutura.zip", "downloads/componentes/conteudoEestrutura")
        Utilitarios.baixarArquivo(listaLinks[2], "Representacao_Conceito_Em_Saude.zip", "downloads/componentes/rep.ConceitoSaude")
        Utilitarios.baixarArquivo(listaLinks[3], "Segurança_Pivacidade.zip", "downloads/componentes/Seguraca_privacidade")
        Utilitarios.baixarArquivo(listaLinks[4], "Comunicação.zip", "downloads/componentes/comunicacao")


    }

    void baixarHistoricoVersoes() {
        String linkVersoes = paginaHome.select("div#parent-fieldname-text :nth-child(6) a").attr("abs:href")
        String htmlBrutoVersoes = this.http.get {
            request.uri = linkVersoes
        }

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("d/M/yyyy")
        LocalDate dataCorte = LocalDate.of(2016, 1, 14)

        Document paginaDeVersoes = Jsoup.parse(htmlBrutoVersoes)

        Elements tabelaAlvo = paginaDeVersoes.select("table tr")

        Elements tabelaFormatada = tabelaAlvo.withIndex().findAll { tr, i ->

            if (i==0){
                return  true
            }

            String textoData = tr.select("td:nth-child(2)").text()
            LocalDate dataDaLinha = LocalDate.parse(textoData, formato)
            return dataDaLinha.isAfter(dataCorte)

        }.collect {it[0]}

        Utilitarios.criadorCsv(tabelaFormatada,"Historico_pós_janeiro_2016", "downloads")

    }

    void  baixarErrosEnvio (){
        String linkTabelasRelacionadas = paginaHome.select("div#parent-fieldname-text :nth-child(8) a").attr("abs:href")

        String htmlBrutotabelasRelacionadas = this.http.get{request.uri = linkTabelasRelacionadas}

        Document tabelasRelacionadas = Jsoup.parse(htmlBrutotabelasRelacionadas)

        String linkTabelaErros = tabelasRelacionadas.select("a.internal-link").attr("abs:href")

        Utilitarios.baixarArquivo(linkTabelaErros,"tabela_de_erros_de_envio_ANS.xlsx","downloads")
    }




}