/* 
 * Copyright (C) 2014 Information Retrieval Group at Universidad Autonoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.uam.eps.ir.ranksys.diversity.binom.reranking;

import es.uam.eps.ir.ranksys.core.IdDouble;
import es.uam.eps.ir.ranksys.core.feature.FeatureData;
import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.diversity.binom.BinomialModel;
import es.uam.eps.ir.ranksys.diversity.reranking.LambdaReranker;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 */
public class BinomialDiversityReranker<U, I, F> extends LambdaReranker<U, I> {

    private final BinomialCoverageReranker<U, I, F> coverageReranker;
    private final BinomialNonRedundancyReranker<U, I, F> nonRedundancyReranker;
    
    public BinomialDiversityReranker(FeatureData<I, F, ?> featureData, BinomialModel<U, I, F> binomialModel, double lambda, int cutoff1, int cutoff2) {
        super(lambda, cutoff1, cutoff2, true);
        coverageReranker = new BinomialCoverageReranker<>(featureData, binomialModel, lambda, cutoff1, cutoff2);
        nonRedundancyReranker = new BinomialNonRedundancyReranker<>(featureData, binomialModel, lambda, cutoff1, cutoff2);
    }

    @Override
    protected LambdaUserReranker getUserReranker(Recommendation<U, I> recommendation) {
        return new BinomialDiversityUserReranker(recommendation);
    }

    protected class BinomialDiversityUserReranker extends LambdaUserReranker {

        private final BinomialCoverageReranker<U, I, F>.BinomialCoverageUserReranker coverageUserReranker;
        private final BinomialNonRedundancyReranker<U, I, F>.BinomialNonRedundancyUserReranker nonRedundancyUserReranker;
        
        public BinomialDiversityUserReranker(Recommendation<U, I> recommendation) {
            super(recommendation);
            this.coverageUserReranker = (BinomialCoverageReranker<U, I, F>.BinomialCoverageUserReranker) coverageReranker.getUserReranker(recommendation);
            this.nonRedundancyUserReranker =  (BinomialNonRedundancyReranker<U, I, F>.BinomialNonRedundancyUserReranker) nonRedundancyReranker.getUserReranker(recommendation);
        }

        @Override
        protected double nov(IdDouble<I> itemValue) {
            return coverageUserReranker.nov(itemValue) * nonRedundancyUserReranker.nov(itemValue);
        }

        @Override
        protected void update(IdDouble<I> bestItemValue) {
            coverageUserReranker.update(bestItemValue);
            nonRedundancyUserReranker.update(bestItemValue);
        }

    }

}
