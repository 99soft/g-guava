package org.nnsoft.guice.gguava.eventbus;

/*
 *    Copyright 2012 The 99 Software Foundation
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

import static com.google.inject.internal.util.$Preconditions.checkArgument;
import static com.google.inject.matcher.Matchers.any;

import static com.google.inject.name.Names.named;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matcher;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

/**
 * This class was originally developed by <a href="http://spin.atomicobject.com/author/dewind/">Justin DeWind</a>
 * on <a href="http://spin.atomicobject.com/2012/01/13/the-guava-eventbus-on-guice/">Atomicobject</a> blog, under the
 * therms of the MIT License.
 */
public final class EventBusModule
    extends AbstractModule
{

    private final String identifier;

    private final Matcher<Object> matcher;

    public EventBusModule( String identifier )
    {
        this( identifier, any() );
    }

    public EventBusModule( String identifier, Matcher<Object> matcher )
    {
        checkArgument( identifier != null, "Event bus identifier must be not null" );
        checkArgument( matcher != null, "Event bus matcher must be not null" );

        this.identifier = identifier;
        this.matcher = matcher;
    }

    @Override
    protected void configure()
    {
        final EventBus eventBus = new EventBus( identifier );

        bind( EventBus.class ).annotatedWith( named( identifier ) ).toInstance( eventBus );

        bindListener( matcher, new TypeListener()
        {
            public <I> void hear( TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter )
            {
                typeEncounter.register( new InjectionListener<I>()
                {
                    public void afterInjection( I injectee )
                    {
                        eventBus.register( injectee );
                    }
                } );
            }
        } );
    }

}