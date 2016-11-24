package teste;

import static teste.CriticidadeDeRequisito.Peso.FORTE;
import static teste.CriticidadeDeRequisito.Peso.FRACO;
import static teste.CriticidadeDeRequisito.Peso.MEDIO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import teste.CriticidadeDeRequisito.Conjuntos.Entradas;
import teste.CriticidadeDeRequisito.Conjuntos.Saidas;

public class CriticidadeDeRequisito {
	static enum Peso {
		FORTE, MEDIO, FRACO
	}

	static class Conjuntos {
		static enum Entradas {
			BAIXA, ALTA
		}

		static enum Saidas {
			BAIXA,ALTA
		}
	}

	public static void main(String[] args) {
		List<Entradas> conjuntos = Arrays.asList(Entradas.values());
		No gr1 = new No("GR1", conjuntos);
		No gr2 = new No("GR2", conjuntos);
		No gr3 = new No("GR3", conjuntos);
		No gr4 = new No("GR4", conjuntos);
		No cr1 = new No("CR1", conjuntos);
		No cr2 = new No("CR2", conjuntos);
		No cr3 = new No("CR3", conjuntos);
		No cr4 = new No("CR4", conjuntos);
		No dr1 = new No("DR1", conjuntos);
		No dr2 = new No("DR1", conjuntos);
		No dr3 = new No("DR1", conjuntos);
		No dr4 = new No("DR1", conjuntos);

		gr1.une(cr2, MEDIO);
		gr1.une(cr3, FRACO);
		gr1.une(cr4, FRACO);
		gr2.une(cr4, MEDIO);
		gr2.une(dr1, MEDIO);
		gr2.une(dr2, FORTE);
		gr2.une(dr3, MEDIO);
		gr2.une(dr4, MEDIO);
		gr3.une(cr2, MEDIO);
		gr3.une(cr3, FRACO);
		gr3.une(cr4, FORTE);
		gr3.une(dr2, FRACO);
		gr3.une(dr3, MEDIO);
		gr4.une(cr1, FRACO);
		gr4.une(cr4, MEDIO);
		cr1.une(dr2, MEDIO);
		cr1.une(dr4, FORTE);
		cr4.une(cr1, MEDIO);
		cr4.une(cr2, MEDIO);
		cr4.une(cr3, MEDIO);
		cr4.une(cr4, MEDIO);

		Integer comprimentodoCaminhos = 3;
		Integer numeroDeCaminhos = 3;
		int contador=121;
		EstrategiaAleatoria modelador = new EstrategiaAleatoria(gr2, comprimentodoCaminhos, numeroDeCaminhos);
		List<Caminho> caminhos = modelador.gerarCaminhos();
		for (Caminho caminho : caminhos) {
			final GeradorDeRegra gerador = new GeradorDeRegra(caminho);
			// System.out.println(gerador.criticiadadeTotal);
			List<Regra> regras = gerador.obterRegras();
			for (Regra regra : regras) {
				System.out.printf("RULE %s : IF ",contador++);
				for (Tupla relacao : regra.relacoes) {
					System.out.printf("%s IS %s AND ", relacao.caminho.no.nome, relacao.entrada);
				}
				Tupla ultimo = regra.relacoes.get(regra.relacoes.size() - 1);
				System.out.printf("%s IS %s THEN CRITICIDADE IS %s;\n", ultimo.caminhoProximo.no.nome,
						ultimo.entradaProxima, regra.saida);
				// System.out.printf("R%s T%s
				// I%s\n",regra.criticidade,gerador.criticiadadeTotal,regra.criticidade/gerador.criticiadadeTotal);
			}
			System.out.println();
		}
	}

	private static final int QUANTIDADE_DE_PESOS = Peso.values().length;

	static class GeradorDeRegra {

		private List<Regra> regras;
		private Caminho caminho;
		private Integer criticiadadeTotal;

		public GeradorDeRegra(Caminho caminho) {
			this.caminho = caminho;
			this.regras = new ArrayList<>();
			this.criticiadadeTotal = criticidadeTotal();
			gerarRegras(caminho, new Regra());

		}

		private void gerarRegras(Caminho caminho, Regra umaRegra) {
			if (caminho.alvo != null) {
				for (Entradas entrada : caminho.no.conjuntos) {
					Regra clone = umaRegra.clonar();
					clone.adicionar(caminho, entrada);
					gerarRegras(caminho.alvo, clone);
				}
			} else {
				for (Entradas entrada : caminho.no.conjuntos) {
					Regra clone = umaRegra.clonar();
					clone.adicionaEntradaNoFinal(entrada, this, caminho);
					regras.add(clone);
				}

			}
		}

		private Integer criticidadeTotal() {
			Caminho caminho = this.caminho;
			int criticiadadeI = 0;
			while (caminho.alvo != null) {
				int quantidadeDeConjuntosEntradaA = caminho.no.conjuntos.size();
				int quantidadeDeConjuntosEntradaB = caminho.alvo.no.conjuntos.size();
				criticiadadeI += (quantidadeDeConjuntosEntradaA * QUANTIDADE_DE_PESOS)
						+(quantidadeDeConjuntosEntradaB * QUANTIDADE_DE_PESOS);
				caminho = caminho.alvo;
			}
			return criticiadadeI;
		}

		public List<Regra> obterRegras() {
			return regras;

		}
	}

	static class Regra {

		private static final float QUANTIDADE_DE_SAIDAS = Saidas.values().length;
		public Integer criticidade;
		private List<Tupla> relacoes;
		private Saidas saida;

		public Regra() {
			relacoes = new ArrayList<>();
		}

		public void adicionaEntradaNoFinal(Entradas entradaNoFinal, GeradorDeRegra geradorDeRegra,
				Caminho caminhoFinal) {
			relacoes.get(relacoes.size() - 1).fixarEntradaProxima(entradaNoFinal, caminhoFinal);
			criticidade = calcularCriticidade();
			gerarSaida(geradorDeRegra);

		}

		private void gerarSaida(GeradorDeRegra geradorDeRegra) {
			float probabilidade = (float) criticidade / geradorDeRegra.criticiadadeTotal;
			int index = (int) (probabilidade * (QUANTIDADE_DE_SAIDAS-0.001));
			saida = Saidas.values()[index];
		}

		private Integer calcularCriticidade() {
			this.criticidade = 0;
			for (Tupla tupla : relacoes) {
				int ordinal = tupla.entrada.ordinal() + 1;
				int peso = tupla.caminho.peso.ordinal() + 1;
				this.criticidade += (ordinal * peso);
				if (tupla.entradaProxima != null) {
					int ordinalProximo = tupla.entradaProxima.ordinal() + 1;
					criticidade += (ordinalProximo * peso);
				}
			}

			return criticidade;
		}

		public void adicionar(Caminho caminho, Entradas entrada) {
			relacoes.add(new Tupla(caminho, entrada));
		}

		Regra clonar() {
			Regra clone = new Regra();
			clone.relacoes = new ArrayList<>();
			for (Tupla tupla : relacoes) {
				Tupla umaTupla = new Tupla(tupla.caminho, tupla.entrada);
				umaTupla.fixarEntradaProxima(tupla.entradaProxima, tupla.caminhoProximo);
				clone.relacoes.add(umaTupla);
			}
			return clone;
		}
	}

	static class Tupla {

		public Entradas entradaProxima;
		private Caminho caminhoProximo;
		private Caminho caminho;
		private Entradas entrada;

		public Tupla(Caminho caminho, Entradas entrada) {
			this.caminho = caminho;
			this.entrada = entrada;
		}

		public void fixarEntradaProxima(Entradas entradaNoFinal, Caminho caminho) {
			this.entradaProxima = entradaNoFinal;
			this.caminhoProximo = caminho;

		}

	}

	static class EstrategiaAleatoria {

		private Integer comprimentoDoCaminho;
		private Integer numeroDeCaminhos;
		private No no;
		private List<Caminho> caminhos;

		public EstrategiaAleatoria(No inicio, Integer comprimentoDoCaminho, Integer numeroDeCaminhos) {
			this.no = inicio;
			this.comprimentoDoCaminho = comprimentoDoCaminho;
			this.numeroDeCaminhos = numeroDeCaminhos;
		}

		public List<Caminho> gerarCaminhos() {
			caminhos = new ArrayList<Caminho>();
			for (int i = 0; i < numeroDeCaminhos; i++) {
				Caminho caminho = new Caminho();
				Integer profundiadade = 0;
				gerarUmCaminho(caminho, profundiadade);
				caminhos.add(caminho);

			}
			return caminhos;
		}

		private void gerarUmCaminho(Caminho caminho, Integer profundidade) {
			caminho.no = no;
			if (profundidade < comprimentoDoCaminho - 1) {
				Collections.shuffle(no.relacoes);
				final Relacao relacao = no.relacoes.get(0);
				caminho.peso = relacao.peso;
				no = relacao.no;
				caminho.alvo = new Caminho();
				gerarUmCaminho(caminho.alvo, ++profundidade);
			}
		}
	}

	static class Caminho {
		private No no;
		private Peso peso;
		private Caminho alvo;
	}

	static class No {
		private List<Relacao> relacoes;
		private String nome;
		private List<Entradas> conjuntos;

		public No(String nome, List<Entradas> conjuntos) {
			this.nome = nome;
			this.conjuntos = conjuntos;
			this.relacoes = new ArrayList<>();
		}

		public void une(No no, Peso peso) {
			relacoes.add(new Relacao(no, peso));
			reverso(no, peso);
		}

		private void reverso(No no, Peso peso) {
			no.relacoes.add(new Relacao(this, peso));
		}
	}

	static class Relacao {
		private No no;
		private Peso peso;

		public Relacao(No no, Peso peso) {
			this.no = no;
			this.peso = peso;

		}
	}

}
