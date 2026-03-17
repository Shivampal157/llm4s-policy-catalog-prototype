package org.llm4s.policycatalog

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class PolicyEngineSpec extends AnyWordSpec with Matchers:

  "PolicyEngine.check" should {

    "pass for allowed provider within token limits" in {
      val cfg = ProviderConfigStub("openai", "gpt-4o-mini", 16000)
      val policy = CatalogPolicies.prodSafe.withMaxTokens(Environment.Prod, 20000)

      val violations = PolicyEngine.check(Environment.Prod, policy, cfg)
      violations shouldBe empty
    }

    "fail when provider not allowed" in {
      val cfg = ProviderConfigStub("ollama", "llama3", 8000)
      val policy = CatalogPolicies.prodSafe

      val violations = PolicyEngine.check(Environment.Prod, policy, cfg)
      violations.exists(_.rule == "allowedProviders") shouldBe true
    }
  }
