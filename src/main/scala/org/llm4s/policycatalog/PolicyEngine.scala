package org.llm4s.policycatalog

final case class ProviderConfigStub(
  provider: String,
  model: String,
  contextWindow: Int
)

final case class Violation(rule: String, message: String)

object PolicyEngine:

  def check(
    env: Environment,
    policy: CatalogPolicy,
    cfg: ProviderConfigStub
  ): List[Violation] =
    val prov = cfg.provider.toLowerCase

    val v1 =
      if policy.allowedProviders.nonEmpty && !policy.allowedProviders(prov) then
        List(Violation(
          "allowedProviders",
          s"Provider '$prov' is not allowed in $env (allowed: ${policy.allowedProviders.mkString(", ")})"
        ))
      else Nil

    val v2 =
      policy.maxTokensPerEnv.get(env).toList.flatMap { max =>
        if cfg.contextWindow > max then
          List(Violation(
            "maxTokens",
            s"contextWindow ${cfg.contextWindow} exceeds max $max for $env"
          ))
        else Nil
      }

    (v1 ++ v2).sortBy(_.rule)
