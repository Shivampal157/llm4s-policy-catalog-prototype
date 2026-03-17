package org.llm4s.policycatalog

object CliCheck:

  def main(args: Array[String]): Unit =
    val env =
      args.headOption match
        case Some("dev")     => Environment.Dev
        case Some("staging") => Environment.Staging
        case _               => Environment.Prod

    val cfg = ProviderConfigStub(
      provider = sys.env.getOrElse("LLM_PROVIDER", "openai"),
      model = sys.env.getOrElse("LLM_MODEL", "openai/gpt-4o"),
      contextWindow = sys.env.get("LLM_CONTEXT_WINDOW").flatMap(_.toIntOption).getOrElse(128000)
    )

    val policy =
      if env == Environment.Dev then CatalogPolicies.devRelaxed
      else CatalogPolicies.prodSafe

    val violations = PolicyEngine.check(env, policy, cfg)

    if violations.isEmpty then
      println(s"[OK] Catalog policy satisfied for $env and provider=${cfg.provider}, model=${cfg.model}")
      sys.exit(0)
    else
      Console.err.println(s"[FAIL] Catalog policy violations for $env:")
      violations.foreach(v => Console.err.println(s"  - [${v.rule}] ${v.message}"))
      sys.exit(1)
