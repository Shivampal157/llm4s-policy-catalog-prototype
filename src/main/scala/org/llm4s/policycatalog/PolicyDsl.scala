package org.llm4s.policycatalog

final case class CatalogPolicy(
  allowedProviders: Set[String] = Set.empty,
  maxTokensPerEnv: Map[Environment, Int] = Map.empty
):

  def allowProviders(ps: String*): CatalogPolicy =
    copy(allowedProviders = ps.map(_.toLowerCase).toSet)

  def withMaxTokens(env: Environment, max: Int): CatalogPolicy =
    copy(maxTokensPerEnv = maxTokensPerEnv + (env -> max))

object CatalogPolicies:
  val devRelaxed: CatalogPolicy =
    CatalogPolicy().allowProviders("openai", "anthropic", "ollama")

  val prodSafe: CatalogPolicy =
    CatalogPolicy()
      .allowProviders("openai", "anthropic", "azure")
      .withMaxTokens(Environment.Prod, 128000)
