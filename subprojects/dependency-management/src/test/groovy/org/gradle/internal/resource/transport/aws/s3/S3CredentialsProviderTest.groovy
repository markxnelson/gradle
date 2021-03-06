/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.internal.resource.transport.aws.s3

import com.amazonaws.auth.InstanceProfileCredentialsProvider
import spock.lang.Specification

class S3CredentialsProviderTest extends Specification {

    def "should create with basic credentials first "() {
        S3CredentialsProvider credentialsProvider = new S3CredentialsProvider('key', 'secret')

        when:
        def chain = credentialsProvider.getChain()

        then:
        chain.credentialsProviders[0] in S3CredentialsProvider
        chain.credentialsProviders[1] in InstanceProfileCredentialsProvider
    }
}
